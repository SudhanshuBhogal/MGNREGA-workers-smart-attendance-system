require("dotenv").config();
var express = require("express"),
    app = express(),
    bodyParser = require("body-parser");

app.set("view engine", "ejs");
app.use(express.static("public"));
app.use(bodyParser.urlencoded({ extended: true }));

//home route
app.get("/", (req, res) => {
    res.render("home",{hideNavbarItems:false});
});

//register route
app.get("/register/:designation", (req, res) => {
    let designation = req.params.designation;
    // console.log(designation);
    if (designation === "authorised-personnel") {
        res.render("register-authorised-personnel",{hideNavbarItems:true});
    } else if (designation==="supervisor") {
        res.render("register-supervisor",{hideNavbarItems:true});
    } else if (designation === "worker") {
        //TODO : pass through middleware to ensure supervisor/authorised personnel is logged in
        res.render("register-worker",{hideNavbarItems:true});
    }
    else {
        //TODO : redirect to 404 page.
        res.send("Invalid Request!!");
    }
});

//login route
app.get("/login/:designation", (req, res) => {
    let designation = req.params.designation;
    // console.log(designation);
    if (designation === "authorised-personnel") {
        res.render("login-authorised-personnel",{hideNavbarItems:true});
    } else if (designation==="supervisor") {
        res.render("login-supervisor",{hideNavbarItems:true});
    } else {
        //TODO : redirect to 404 page.
        res.send("Invalid Request!!");
    }
});

app.listen(process.env.PORT, process.env.IP, () => {
    console.log("Server started at port " + process.env.PORT);
});
