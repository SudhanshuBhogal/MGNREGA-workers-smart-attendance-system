const { response } = require("express");

require("dotenv").config();
var express = require("express"),
    app = express(),
    bodyParser = require("body-parser"),
    mongoose = require("mongoose"),
    bcrypt = require("bcryptjs"),
    passport = require("passport"),
    LocalStrategy = require("passport-local"),
    session = require("express-session"),
    fs = require('fs'),
    path = require('path'),
    multer = require('multer'),
    request = require('request'),
    Worker = require("./models/Worker"),
    WorkingSite = require("./models/WorkingSite"),
    AuthorisedPersonnel = require("./models/AuthorisedPersonnel"),
    Supervisor = require("./models/Supervisor"),
    Image = require("./models/Image");

// Passport Config
require("./config/passport")(passport);

mongoose.connect(process.env.DATABASEURL, { useNewUrlParser: true, useUnifiedTopology: true }).then(() => console.log('Connected to DB!')).catch(error => console.log(error.message));

//multer middleware
var storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads')
    },
    filename: (req, file, cb) => {
        cb(null, file.fieldname + '-' + Date.now() + path.extname(file.originalname));
    }
});

var upload = multer({ storage: storage });

app.set("view engine", "ejs");
app.use(express.static("public"));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

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

//register worker, get image vectos and store the worker in the database
app.post("/worker", upload.single('faceImage'), (req, res, next) => {
    console.log(req.body.worker);
    var imageObject = {
        name: req.body.worker.jobCardId,
        image: {
            data: fs.readFileSync(path.join(__dirname + '/uploads/' + req.file.filename)),
            contentType: 'image/png'
        }
    }
    // for getting embedding from image
    request.post({
        url: "http://localhost:5000/getfacevector",
        form: {imageBase64 : imageObject.image.data.toString('base64')}
        }, (err, res, body) => {
        if (!err && res.statusCode == 200) {
            var faceMappings = JSON.parse(body);
            console.log(faceMappings);
        }
    });

    Image.create(imageObject, (err, savedImage) => {
        if (err) {
            console.log(err);
            res.redirect("..");
        } else {
            Worker.create(req.body.worker, (err, newWorker) => {
                if (err) {
                    console.log(err);
                    res.redirect("..");
                } else {
                    newWorker.faceImage = savedImage;
                    newWorker.save((err, newWorkerData) => {
                        if (err) {
                            console.log(err);
                            res.redirect("..");
                        } else {
                            console.log(newWorkerData);
                        }
                    });
                    console.log(newWorker);
                    res.send("Worker registered successfully!");
                }
            });
        }
    });


});

//worker show route
app.get("/workers/:page", async (req, res) => {
    // const resutlsperpage = 3;
    // const page = req.params.page || 1;
    // Worker.find({}, function(err, workers) {
    //     console.log(workers);
    //     res.render("workers-show",{workers:workers});
    // });
    // Declaring variable
    const resPerPage = 10; // results per page
    const page = req.params.page || 1; // Page 
    try {
        const foundWorkers = await Worker.find({})
            .skip((resPerPage * page) - resPerPage)
            .limit(resPerPage);
        const numOfWorkers = await Worker.count({});
        // Renders The Page
        res.render('workers-show', {
            workers: foundWorkers,
            currentPage: page,
            pages: Math.ceil(numOfWorkers / resPerPage),
            numOfResults: numOfWorkers
        });
    } catch (err) {
        throw new Error(err);
    }
});

//workers attendance show route
app.get("/worker/:id/attendance", (req, res) => {
    console.log(req.params.id);
    Worker.findOne({ _id: req.params.id }, (err, foundWorker) => {
        if (err) {
            console.log(err);
        } else {
            console.log(foundWorker);
            res.render("show-attendance", { worker: foundWorker });
        }
    });
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
    WorkingSite.create(req.body.workingSite, (err, newWorkingSite) => {
        if (err) {
            console.log(err);
            res.redirect("..");
        } else {
            console.log(newWorkingSite);
            res.send("Worker registered successfully!");
        }
    });
});

//api for android
app.post("/markattendance", (req, res) => {
    console.log(req.body.name);
    Worker.findOne({ firstName: req.body.name }, function (err, worker) {
        if (err) {
            console.log(err);
        } else {
            let record = {
                date: Date.now(),
                latitude: req.body.latitude,
                longitude: req.body.longitude,
            };
            console.log("Saving attendance record : " + record);
            worker.attendanceRecord.push(record);
            worker.save((err, savedWorker) => {
                if (err) {
                    console.log(err);
                } else {
                    console.log(savedWorker);
                    let ret = {
                        contactNumber: savedWorker.contactNumber,
                        status: "ok",
                    };
                    console.log("Marked Attendance successfully");
                    console.log("Sending back : " + ret);
                    res.json(ret);
                }
            });
        }
    });
});

// app.get("/markattendance", (req, res) => {
//     res.send("Reached sudhanshu successsfully!");
// });




app.listen(process.env.PORT, process.env.IP, () => {
    console.log("Server started at port " + process.env.PORT);
});
