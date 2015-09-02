var express = require('express');
var app = express();
// Default Content-Type to Application/Json
app.use(function(req, res, next) { res.setHeader("Content-Type", "application/json"); return next(); });

// Koodisto-Service
app.use(express.static(__dirname + '/static'));

// Vetuma
app.post('/VETUMAPayment', function(req, res){
  res.send({ "personOid": "1.2.246.562.24.11523238937" });
});

// Authentication-Service
app.post('/authentication-service/resources/s2s/hakuperusteet', function(req, res){
  res.send({ "personOid": "1.2.246.562.24.11523238937" });
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
