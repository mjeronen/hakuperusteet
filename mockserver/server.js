var express = require('express');
var requestify = require('requestify');
var crypto = require('crypto')
process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";

var app = express();

//var ciphers = crypto.getCiphers();
//console.log(ciphers);

// Default Content-Type to Application/Json
app.use(function(req, res, next) { res.setHeader("Content-Type", "application/json"); return next(); });

// Koodisto-Service
app.use(express.static(__dirname + '/static'));



// Authentication-Service
app.post('/authentication-service/resources/s2s/hakuperusteet', function(req, res){
  res.send({ "personOid": "1.2.246.562.24.11523238937" });
});

var callback = function(url) {
    console.log("Sending POST request to " + url);
    requestify.post(url, {})
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
  var v = op.join('&') + "&" + SHARED_SECRET + "&"
  console.log(v);
  var sha256 = crypto.createHash('sha256');
  sha256.update(v)
  var mac = sha256.digest('hex').toUpperCase()
  callback(p["RETURL"] + "?" + op.join('&') + "&MAC=" + mac)
  res.writeHead(200, {'Content-Type': 'text/html'});
  res.write("<form action=\"" + "https://localhost:18080/hakuperusteet/?result=ok" +"\"><input type=\"submit\" value=\"Palaa myyjän palveluun\"></form>");
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

app.listen(process.env.PORT || 3000);
