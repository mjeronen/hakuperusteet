var express = require('express')
var requestify = require('requestify')
var crypto = require('crypto')
var bodyParser = require('body-parser')
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

var ldap = require('ldapjs');
var server = ldap.createServer();
server.bind('ou=People, dc=opintopolku, dc=fi', function(req, res, next) {
  console.log('bind DN: ' + req.dn.toString());
  console.log('bind PW: ' + req.credentials);
  // AUTH CHECK WOULD BE HERE BUT PASSING ALL
  res.end();
  return next();
});

server.search('ou=People, dc=opintopolku, dc=fi', function(req, res, next) {
  var obj = {
    dn: 'uid=testitest,ou=People,dc=opintopolku,dc=fi',
    attributes: {
      employeeNumber: "1.2.246.562.24.00000001337",
      uid: "testitest",
      sn: "Testaaja",
      givenname: "Testi",
      description: '["ROLE1", "ROLE2", "ROLE3"]'
    }
  };
  res.send(obj);
  res.end();
});

server.listen(1389, function() {
  console.log('ldapjs listening at ' + server.url);
});

var app = express();
// Body parser
app.use(bodyParser.json())
// Default Content-Type to Application/Json
app.use(function(req, res, next) { res.setHeader("Content-Type", "application/json"); return next(); });

// Koodisto-Service
app.use(express.static(__dirname + '/static'));

// Ryhmasahkoposti-Service
app.post('/ryhmasahkoposti-service/email', function(req, res){
  console.log("Sending email: " + JSON.stringify(req.body))
  res.send({});
});

// Authentication-Service
app.post('/authentication-service/resources/s2s/hakuperusteet', function(req, res){
  res.send({ "personOid": "1.2.246.562.24.11523238937" });
});

var callback_get = function(url) {
  console.log("Sending GET request to " + url);
  requestify.request(url, {
    method: 'GET'
  }).then(function(response) {
    console.log(response)
  });
}

// Oppijan-tunnistus
var oppijanTunnistusEmails = {}
app.post('/oppijan-tunnistus/api/v1/token', function(req, res){
  var sha256 = crypto.createHash('sha256');
  sha256.update(req.body.email + Date.now())
  var token = sha256.digest('hex');
  console.log("Sending verification email: " + JSON.stringify(req.body) + " with token " + token)
  var callback_url = req.body.url + token;
  console.log("Callback URL is ");
  console.log(callback_url);
  oppijanTunnistusEmails[token] = req.body.email;
  res.send(callback_url);
});
app.get('/oppijan-tunnistus/api/v1/token/:token', function(req, res){
  var token = req.params.token
  console.log("Verifying token " + token)
  if (token == "mochaTestToken") {
    res.send({ "valid" : true, "email" : "mochatest@example.com"});
  } else {
    var email = oppijanTunnistusEmails[token]
    if(email) {
      res.send({ "valid" : true, "email" : email});
    } else {
      res.send({ "valid" : false });
    }
  }
});

var callback = function(url, params) {
    console.log("Sending POST request to " + url);
    requestify.request(url, {
      method: 'POST',
      params: params
    })
    .then(function(response) {
        console.log(response)
    });
}

// Vetuma
app.post('/VETUMAPayment', function(req, res){
  var p = req.query
  var SO = "P2"
  var PAYID = "441265046723995"
  var PAID = "15092588INWX0000"
  var STATUS = "SUCCESSFUL"
  var SHARED_SECRET = "TESTIASIAKAS11-873C992B8C4C01EC8355500CAA709B37EA43BC2E591ABF29FEE5EAFE4DCBFA35"
  var TIMESTAMP = "20150903102417186"
  var op = [p["RCVID"], TIMESTAMP, SO, p["LG"], p["RETURL"], p["CANURL"], p["ERRURL"], PAYID, p["REF"], p["ORDNR"], PAID, STATUS]
  var sha256 = crypto.createHash('sha256');
  sha256.update(op.join('&') + "&" + SHARED_SECRET + "&")
  var mac = sha256.digest('hex').toUpperCase()
  callback(p["RETURL"], {
    "RCVID": p["RCVID"], "TIMESTMP" : TIMESTAMP, "SO": SO, "LG": p["LG"], "RETURL": p["RETURL"], "CANURL": p["CANURL"],
    "ERRURL": p["ERRURL"], "PAYID": PAYID, "REF": p["REF"], "ORDNR" : p["ORDNR"], "PAID": PAID, "STATUS" : STATUS, "MAC" : mac
  });
  console.log("Responding from Vetuma with MAC = " + mac);
  res.writeHead(200, {'Content-Type': 'text/html'});
  res.write("<form action=\"" + "https://localhost:18080/hakuperusteet/?result=ok" +"\"><input type=\"submit\" value=\"Palaa myyj&auml;n palveluun\"></form>");
  res.end();
});

// CAS
app.post('/cas/v1/tickets', function(req, res){
  res.append('Location', 'http://localhost:3000/cas/v1/tickets/TGT-123');
  res.status(201)
  res.send({});
});
app.post('/cas/v1/tickets/TGT-123', function(req, res){
  res.send("ST-123");
});
app.get('/authentication-service/j_spring_cas_security_check', function(req, res){
  res.append('Set-Cookie', 'JSESSIONID=foobar-123');
  res.send({});
});
app.get('/ryhmasahkoposti-service/j_spring_cas_security_check', function(req, res){
  res.append('Set-Cookie', 'JSESSIONID=foobar-123');
  res.send({});
});
app.listen(process.env.PORT || 3000);
