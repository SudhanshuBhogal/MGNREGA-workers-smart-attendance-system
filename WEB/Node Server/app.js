const { response } = require("express");
const { runInNewContext } = require("vm");

require("dotenv").config();
var express = require("express"),
    app = express(),
    request = require('request'),
    bodyParser = require("body-parser"),

    //database and authenticatin imports
    mongoose = require("mongoose"),
    bcrypt = require("bcryptjs"),
    passport = require("passport"),
    LocalStrategy = require("passport-local"),
    session = require("express-session"),

    //imports for image upload
    fs = require('fs'),
    path = require('path'),
    multer = require('multer'),

    //importing models
    Worker = require("./models/Worker"),
    WorkingSite = require("./models/WorkingSite"),
    AuthorisedPersonnel = require("./models/AuthorisedPersonnel"),
    Supervisor = require("./models/Supervisor"),
    Image = require("./models/Image"),
    Workday = require("./models/Workday");

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

//workers route start here
//new worker route
app.get("/worker/new", (req, res) => {
    res.render("register-worker");
})

//register worker, get image vectos and store the worker in the database
app.post("/worker", upload.single('faceImage'), async (req, res, next) => {
    console.log(req.body.worker);
    var imageObject = {
        name: req.body.worker.jobCardId,
        image: {
            data: fs.readFileSync(path.join(__dirname + '/uploads/' + req.file.filename)),
            contentType: 'image/png'
        }
    }
    // // for getting embedding from image
    // await request.post({
    //     url: "http://localhost:5010/getfacevector",
    //     form: { imageBase64: imageObject.image.data.toString('base64') }
    // }, (err, res, body) => {
    //     console.log("Reached here 2");
    //     if (err) {
    //         console.log(err);
    //     }
    //     if (!err && res.statusCode == 200) {
    //         var faceMappings = JSON.parse(body);
    //         console.log(faceMappings);
    //     }
    // });

    await Image.create(imageObject, (err, savedImage) => {
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
    Worker.findById(req.params.id, (err, foundWorker) => {
        if (err) {
            console.log(err);
        } else {
            console.log(foundWorker);
            res.render("show-attendance", { worker: foundWorker });
        }
    });
})

//worker profile route
app.get("/worker/:id", (req, res) => {
    console.log(req.params.id);
    Worker.findById(req.params.id, (err, foundWorker) => {
        if (err) {
            console.log(err);
        } else {
            console.log(foundWorker);
            res.render("worker-profile", { worker: foundWorker });
        }
    });
});

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

//get absent workers api for android
app.get("/absent-workers", (req, res) => {
    let day = req.query.day;
    let month = req.query.month;
    let year = req.query.year;
    var d = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
    var date = d.toLocaleDateString();
    Workday.findOne({ date: date }, async (err, foundWorkday) => {
        if (err) {
            console.log(err);
        } else {
            if (!foundWorkday) {
                res.json({ status: "error", message: "workday not found in db" });
                return;
            }
            let uniqueWorkers = foundWorkday.presentWorkers.filter((item, i, ar) => ar.indexOf(item) === i);
            Worker.find().where('_id').nin(uniqueWorkers).exec((err, foundWorkers) => {
                if (err) {
                    console.log(err);
                } else {
                    let ret = [];
                    foundWorkers.forEach((foundWorker) => {
                        let workerInfo = {
                            name: foundWorker.firstName + " " + foundWorker.lastName,
                            contactNumber: foundWorker.contactNumber,
                        }
                        ret.push(workerInfo);
                    });
                    res.json(ret);
                }
            });
        }
    });
});

//mark attendance api for android
app.post("/markattendance", (req, res) => {
    console.log(req.body.name);
    const authkey = req.body.authKey;
    if (!authkey || authkey !== process.env.ANDROIDAPKAUTHKEY) {
        res.json({ error: "Access denied", status: "failed" });
        return;
    }
    // console.log(req.body.encoded);
    const base64string = req.body.encoded.replace(/^data:image\/(png|jpeg|jpg);base64,/, '');
    const buffer = Buffer.from(base64string, 'base64');

    Worker.findOne({ firstName: req.body.name }, function (err, worker) {
        if (err) {
            console.log(err);
        } else {
            let record = {
                date: new Date(),
                latitude: req.body.latitude,
                longitude: req.body.longitude,
                fullAddress: req.body.address,
                city: req.body.city,
                pincode: req.body.postalCode,
                base64img: {
                    data: buffer,
                    contentType: "image/png"
                }
            };
            //finding workday, if present then push the worker to it or otherwise make new workday and then push the worker in it
            Workday.findOne({ date: record.date.toLocaleDateString() }, (err, workday) => {
                if (err) {
                    console.log(err);
                } else if (workday) {
                    // console.log(workday);
                    workday.presentWorkers.push(worker);
                    workday.save((err, savedWorkday) => {
                        if (err) {
                            console.log(err);
                        } else {
                            console.log("Saving attendance record : " + record);
                            worker.attendanceRecord.push(record);
                            worker.save((err, savedWorker) => {
                                if (err) {
                                    console.log(err);
                                } else {
                                    // console.log(savedWorker);
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
                } else {
                    Workday.create({ date: record.date.toLocaleDateString() }, (err, workday) => {
                        if (err) {
                            console.log(err);
                        } else {
                            // console.log(workday);
                            workday.presentWorkers.push(worker);
                            workday.save((err, savedWorkday) => {
                                if (err) {
                                    console.log(err);
                                } else {
                                    // console.log("Saving attendance record : " + record);
                                    worker.attendanceRecord.push(record);
                                    worker.save((err, savedWorker) => {
                                        if (err) {
                                            console.log(err);
                                        } else {
                                            // console.log(savedWorker);
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
                        }
                    });

                }
            });
        }
    });
});

app.get("/analysis/workers/get", (req, res) => {
    let year = req.query.year;
    let month = req.query.month;
    let day = req.query.day;
    if (!year || year == 0) {
        res.render("workers-analysis", { showAnalysis: false, year: false, month: false, day: false, presentCount: false, totalCount: false });
    } else {
        var d = new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
        var date = d.toLocaleDateString();
        Workday.findOne({ date: date }, async (err, foundWorkday) => {
            if (err) {
                console.log(err);
            } else {
                if (!foundWorkday) {
                    res.send("Work day not found!");
                    return;
                }
                let unique = foundWorkday.presentWorkers.filter((item, i, ar) => ar.indexOf(item) === i);
                let presentCount = unique.length;
                let totalCount = await Worker.count({});
                console.log("Present Count : " + presentCount + " Total Count : " + totalCount);
                res.render("workers-analysis", { showAnalysis: true, year: year, month: month, day: day, presentCount: presentCount, totalCount: totalCount });
            }
        });
    }
});

app.listen(process.env.PORT, process.env.IP, () => {
    console.log("Server started at port " + process.env.PORT);
});


//TODO:
//face mappings calculation
//working site connections
//SMS