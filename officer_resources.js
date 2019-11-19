/*
This file is another example of code I wrote while I was at CSEA. This is part of the API that is used to get Officer 
Resources data from the server. The API is called in officer_resources_include.php twice. Once to get check the privileges
of the user to see what kind of officer they are. The second API call fetches the Officer Resources that is stored in
a database. Which resources are retrieved depends on the ranking of the officer/user. Each row, or resource, in the
database has columns that designate who has access to it with a 'Y' or 'N' for president, vice president, etc. 
I am proud of this piece of code because it demonstrates my ability to work with asynchronous Javascript and also SQL queries.  
*/

module.exports = (function() {
  var router = require("express").Router();

  // This router fetches resources based on the user's officer ranking
  router.get("/titles/:title", async function(req, res) {
    var title = req.params.title.toUpperCase();

    try {
      if (title == "PRES") {
        var queryString =
          "select * from web_officer_resources where active='A' and PRES = 'Y' order by resource_title";
        const result = await req.dbConnection.execute(queryString);
        res.send(result.rows);
      } else if (title == "VP") {
        var queryString =
          "select * from web_officer_resources where active='A' and VP = 'Y' order by resource_title";
        const result = await req.dbConnection.execute(queryString);
        res.send(result.rows);
      } else if (title == "RESE") {
        var queryString =
          "select * from web_officer_resources where active='A' and RESE = 'Y' order by resource_title";
        const result = await req.dbConnection.execute(queryString);
        res.send(result.rows);
      } else if (title == "TRES") {
        var queryString =
          "select * from web_officer_resources where active='A' and TRES = 'Y' order by resource_title";
        const result = await req.dbConnection.execute(queryString);
        res.send(result.rows);
      } else if (title == "STEW") {
        var queryString =
          "select * from web_officer_resources where active='A' and STEW = 'Y' order by resource_title";
        const result = await req.dbConnection.execute(queryString);
        res.send(result.rows);
      } else if (title == "GR") {
        var queryString =
          "select * from web_officer_resources where active='A' and GR = 'Y' order by resource_title";
        const result = await req.dbConnection.execute(queryString);
        res.send(result.rows);
      } else if (title == "ALL") {
        var queryString =
          "select * from web_officer_resources where active='A' order by resource_title";
        const result = await req.dbConnection.execute(queryString);
        res.send(result.rows);
      }
    } catch (err) {
      console.log(err);
      res.send(err);
    }
  });

  // This router gets all resources and orders them by title
  router.get("/get/", async function(req, res) {

    var queryString =
      "SELECT * FROM web_officer_resources WHERE active='A' order by resource_title";
    var response = await req.dbConnection.execute(queryString);

    res.status(200).send(response.rows);
  });

  // This router retreives a single resource based on ID
  router.get("/resourcebyid/:resource_id", async function(req, res) {
    var RESOURCE_ID = req.params.resource_id;

    var queryString =
      "select resource_id, description, resource_type, resource_title, data from web_officer_resources where resource_id=:resource_id";

    var response = await req.dbConnection.execute(queryString, [RESOURCE_ID]);

    res.status(200).send(response.rows);
  });

  // This router returns the officer code and title to determine their access and also makes sure they are a current officer
  router.get("/vipinfo/:csea_id", async function(req, res) {
    CSEA_ID = req.params.csea_id;

    var today = new Date();
    var dd = today.getDate();
    var mm = today.getMonth() + 1; //January is 0!
    var yyyy = today.getFullYear();

    if (dd < 10) {
      dd = "0" + dd;
    }
    if (mm < 10) {
      mm = "0" + mm;
    }
    var today = yyyy + "-" + mm + "-" + dd;

    //try {
    var queryString =
      "select office_code,office_title from cis_query.vipoffices,cis.employees where cis_query.vipoffices.ssn=cis.employees.ssn and csea_id= :csea_id and substr(office_start_date, 0, 10) <= '" +
      today +
      "' and (substr(office_end_date, 0, 10) >= '" +
      today +
      "' or office_end_date='0000-00-00 00:00:00')";
    const result = await req.dbConnection.execute(queryString, [CSEA_ID]);
    res.status(200).send(result.rows);
  });

  // This router is used to update a resource
  router.post("/update", async function(req, res) {
    resource_type = req.body.resource_type;
    description = req.body.description;
    active = req.body.active;
    reference_name = req.body.reference_name;
    resource_title = req.body.resource_title;
    release_datetime = req.body.release_datetime;
    expiry_datetime = req.body.expiry_datetime;
    pres = req.body.pres;
    vp = req.body.vp;
    rese = req.body.rese;
    tres = req.body.tres;
    gr = req.body.gr;
    stew = req.body.stew;

    try {
      var queryString =
        "update web_officer_resources set resource_type=:resource_type, description=:description, active=:active, reference_name=:reference_name, resource_title=:resource_title, release_datetime=:release_datetime, expiry_datetime=:expiry_datetime, pres=:pres, vp=:vp, rese=:rese, tres=:tres, gr=:gr, stew=:stew";
      const result = await req.dbConnection.execute(queryString, [
        resource_type,
        description,
        active,
        reference_name,
        resource_title,
        release_datetime,
        expiry_datetime,
        pres,
        vp,
        rese,
        tres,
        gr,
        stew
      ]);

      res.send(result);
    } catch (err) {
      console.log(err);
      res.send(err);
    }
  });

  // This router is used to insert a new resource 
  router.post("/insert", async function(req, res) {
    resource_type = req.body.resource_type;
    description = req.body.description;
    active = req.body.active;
    reference_name = req.body.reference_name;
    resource_title = req.body.resource_title;
    release_datetime = req.body.release_datetime;
    expiry_datetime = req.body.expiry_datetime;
    pres = req.body.pres;
    vp = req.body.vp;
    rese = req.body.rese;
    tres = req.body.tres;
    gr = req.body.gr;
    stew = req.body.stew;

    try {
      var queryString =
        "insert into web_officer_resources (resource_type, description, active, reference_name, resource_title, release_datetime, expiry_datetime, pres, vp, rese, tres, gr, stew) VALUES (:1,:2,:3,:4,:5,:6,:7,:8,:9,:10,:11,:12,:13)";
      const result = await req.dbConnection.execute(queryString, [
        resource_type,
        description,
        active,
        reference_name,
        resource_title,
        release_datetime,
        expiry_datetime,
        pres,
        vp,
        rese,
        tres,
        gr,
        stew
      ]);

      res.send(result);
    } catch (err) {
      console.log(err);
      res.send(err);
    }
  });

  return router;
})();
