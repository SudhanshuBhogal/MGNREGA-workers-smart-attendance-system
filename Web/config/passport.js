//Only implemented for Supervisor authentication
//TODO: implement for authorised personnel also
const LocalStrategy = require('passport-local').Strategy;
const bcrypt = require('bcryptjs');

// // Load Supervisor model
const Supervisor = require('../models/Supervisor');

module.exports = function(passport) {
  passport.use(
    new LocalStrategy({ usernameField: 'email' }, (email, password, done) => {
      // Match supervisor
      Supervisor.findOne({
        email: email
      }).then(supervisor => {
        if (!supervisor) {
          console.log("Email not found!");
          return done(null, false, { message: 'That email is not registered' });
        }

        // Match password
        bcrypt.compare(password, supervisor.password, (err, isMatch) => {
          if (err) throw err;
          if (isMatch) {
            return done(null, supervisor);
          } else {
            console.log("Password mismatch!");
            return done(null, false, { message: 'Password incorrect' });
          }
        });
      });
    })
  );

  passport.serializeUser(function(supervisor, done) {
    done(null, supervisor.id);
  });

  passport.deserializeUser(function(id, done) {
    Supervisor.findById(id, function(err, supervisor) {
      done(err, supervisor);
    });
  });
};
