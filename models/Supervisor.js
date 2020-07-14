const mongoose = require("mongoose");

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

const Supervisor = mongoose.model(
    "Supervisor",
    SupervisorSchema
);

module.exports = Supervisor;
