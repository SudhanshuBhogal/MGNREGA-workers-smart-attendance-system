const mongoose = require("mongoose");

const WorkerSchema = new mongoose.Schema({
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
    jobCardId: {
        type: String,
        required: true,
    },
    faceMappings: {
        type: String
    },
    date: {
        type: Date,
        default: Date.now,
    },
});

const Worker = mongoose.model(
    "Worker",
    WorkerSchema
);

module.exports = Worker;
