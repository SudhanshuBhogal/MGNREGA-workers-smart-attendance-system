require("dotenv").config();
var express = require("express"),
    app = express(),
    bodyParser = require("body-parser"),
    mongoose = require("mongoose"),
    bcrypt = require("bcryptjs"),
    passport = require("passport"),
    LocalStrategy = require("passport-local"),
    session = require("express-session"),
    Worker = require("./models/Worker"),
    WorkingSite = require("./models/WorkingSite"),
    AuthorisedPersonnel = require("./models/AuthorisedPersonnel"),
    Supervisor = require("./models/Supervisor");

// Passport Config
require("./config/passport")(passport);

mongoose.connect(process.env.DATABASEURL, { useNewUrlParser: true, useUnifiedTopology: true }).then(() => console.log('Connected to DB!')).catch(error => console.log(error.message));

app.set("view engine", "ejs");
app.use(express.static("public"));
app.use(bodyParser.urlencoded({ extended: true }));

//passport configuration  
// Express session
app.use(
    session({
        secret: "Abra ka dabra",
        resave: false,
        saveUninitialized: false,
    })
);

// Passport middleware
app.use(passport.initialize());
app.use(passport.session());
// passport.use(new LocalStrategy({ usernameField: 'email' },Supervisor.authenticate()));
// passport.use(new LocalStrategy({ usernameField: 'email' },AuthorisedPersonnel.authenticate()));
// passport.serializeUser(AuthorisedPersonnel.serializeUser());
// passport.deserializeUser(AuthorisedPersonnel.deserializeUser());
// passport.serializeUser(Supervisor.serializeUser());
// passport.deserializeUser(Supervisor.deserializeUser());



//home route
app.get("/", (req, res) => {
    res.render("home", { showNavbarItems: true });
});

//register routes
// get register forms
app.get("/register/:designation", (req, res) => {
    let designation = req.params.designation;
    // console.log(designation);
    if (designation === "authorised-personnel") {
        res.render("register-authorised-personnel");
    } else if (designation === "supervisor") {
        res.render("register-supervisor");
    }
    else {
        //TODO : redirect to 404 page.
        res.send("Invalid Request!!");
    }
});

//TODO
//register Authorised-personnel 
app.post("/register/authorised-personnel", (req, res) => {
    res.send("Yet to be implemented!");
    return;
    if (req.body.password != req.body.password2) {
        res.send("Error!..Passwords do not match!");
    } else {
        password2 = "trash";
    }
    let newAuthorisedPersonnel = new AuthorisedPersonnel(req.body.AuthorisedPersonnel);
    console.log(newAuthorisedPersonnel);
    AuthorisedPersonnel.register(newAuthorisedPersonnel, req.body.password, (error, AuthorisedPersonnel) => {
        if (error) {
            console.log(error);
            return res.render("register-authorised-personnel");
        }
        passport.authenticate("local")(req, res, () => {
            res.redirect("/dashboard/authorised-personnel");
        })
    });
});

//register supervisor
app.post("/register/supervisor", (req, res) => {
    console.log("Password:" + req.body.password);
    let errors = [];
    if (!req.body.password || !req.body.password2) {
        errors.push({ msg: "Please enter all fields" });
    }
    if (req.body.password != req.body.password2) {
        errors.push({ msg: "Passwords do not match" });
    }
    if (req.body.password.length < 6) {
        errors.push({ msg: "Password must be at least 6 characters" });
    }
    if (errors.length > 0) {
        console.log(errors);
        res.render("register-supervisor", { errors: errors });
    } else {
        let newSupervisor = new Supervisor(req.body.Supervisor);
        console.log(newSupervisor);
        Supervisor.findOne({ email: newSupervisor.email }).then((supervisor) => {
            if (supervisor) {
                errors.push({ msg: "Email already exists" });
                console.log(errors);
                res.render("register-supervisor", { errors: errors });
            } else {
                newSupervisor["password"] = req.body.password;
                bcrypt.genSalt(10, (err, salt) => {
                    bcrypt.hash(newSupervisor.password, salt, (err, hash) => {
                        if (err) throw err;
                        newSupervisor.password = hash;
                        newSupervisor
                            .save()
                            .then((supervisor) => {
                                //TODO: successs flash
                                // req.flash(
                                //   "success_msg",
                                //   "You are now registered and can log in"
                                // );
                                res.redirect("/dashboard/supervisor");
                            })
                            .catch((err) => console.log(err));
                    });
                });
            }
        });
    }
});

app.get("/worker/new", (req, res) => {
    res.render("register-worker");
})
//register worker
app.post("/worker", (req, res) => {
    console.log(req.body.worker);
    Worker.create(req.body.worker, (err, newWorker) => {
        if (err) {
            console.log(err);
            res.redirect("..");
        } else {
            console.log(newWorker);
            res.send("Worker registered successfully!");
        }
    })
    
})

//login routes
app.get("/login/:designation", (req, res) => {
    let designation = req.params.designation;
    // console.log(designation);
    if (designation === "authorised-personnel") {
        res.render("login-authorised-personnel");
    } else if (designation === "supervisor") {
        res.render("login-supervisor");
    } else {
        //TODO : redirect to 404 page.
        res.send("Invalid Request!!");
    }
});

//TODO : authorised personnel login/logout

//supervisor login/logout , this is not working as of now
app.post("/login/supervisor", (req, res, next) => {
    passport.authenticate("local", {
        successRedirect: "/dashboard/supervisor",
        failureRedirect: "/login/supervisor",
        failureFlash: false,
    })(req, res, next);
});

//TODO : add dynamic logout button in navbar
app.get("/logout", (req, res) => {
    req.logout();
    alert("You are logged out successfully!");
    res.redirect("/");
});




//dashboard routes
app.get("/dashboard/:designation", (req, res) => {
    if (req.params.designation === "authorised-personnel") {
        return res.render("dashboard-authorised-personnel");
    } else if (req.params.designation === "supervisor") {
        return res.render("dashboard-supervisor");
    } else {
        //TODO : redirect to 404 page.
        res.send("Invalid Request!!");
    }
});

//Working site routes
app.get("/working-site/new", (req, res) => {
    res.render("create-working-site");
})

app.post("/working-site", (req, res) => {
    console.log(req.body.workingSite);
    WorkingSite.create(req.body.workingSite, (err, newWorkingSite)=>{
        if (err) {
            console.log(err);
            res.redirect("..");
        } else {
            console.log(newWorkingSite);
            res.send("Worker registered successfully!");
        }
    });
});


app.listen(process.env.PORT, process.env.IP, () => {
    console.log("Server started at port " + process.env.PORT);
});
