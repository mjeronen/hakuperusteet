var express = require('express');
var app = express();
// Default Content-Type to Application/Json
app.use(function(req, res, next) { res.setHeader("Content-Type", "application/json"); return next(); });

// Koodisto-Service
app.use(express.static(__dirname + '/static'));

// Authentication-Service
app.post('/authentication-service/resources/s2s/hakuperusteet', function(req, res){
  res.send({ "personOid": "1.2.246.562.24.11523238937" });
});

// Vetuma
app.post('/VETUMAPayment', function(req, res){
  res.send({ "personOid": "1.2.246.562.24.11523238937" });
});

app.listen(process.env.PORT || 3000);
