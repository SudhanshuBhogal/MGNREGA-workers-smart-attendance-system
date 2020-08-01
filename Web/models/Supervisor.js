const mongoose = require("mongoose");
const passportLocalMongoose = require("passport-local-mongoose");

const SupervisorSchema = new mongoose.Schema({
    firstName: {
        type: String,
        required: true,
    },
    lastName: {
        type: String,
        required: true,
    },
    addressLine: {
        type: String,
        required: true,
    },
    city: {
        type: String,
        required: true,
    },
    state: {
        type: String,
        required: true,
    },
    pincode: {
        type: Number,
        required: true,
    },
    faceMappings: {
        type: String
    },
    approvedBy: {
        type: String,
        default: "None"
    },
    allocatedSite: {
        type: mongoose.Schema.Types.ObjectId,
        ref : "WorkingSite"
    },
    email: {
        type: String,
        required: true,
    },
    password: {
        type: String,
        required: true,
    },
    date: {
        type: Date,
        default: Date.now,
    },
});

SupervisorSchema.plugin(passportLocalMongoose);
module.exports = mongoose.model(
    "Supervisor",
    SupervisorSchema
);

