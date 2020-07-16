const mongoose = require("mongoose");

const WorkingSiteSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        unique: true,
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
    coordinates: {
        latitude: { type: String },
        longitude: { type: String }
    },
    supervisor: {
        type: mongoose.Schema.Types.ObjectId,
        ref : "Supervisor"
    },
    workers: [
        {
            type: mongoose.Schema.Types.ObjectId,
            ref : "Worker"
        }
    ],
    date: {
        type: Date,
        default: Date.now,
    },
});


module.exports = mongoose.model(
    "WorkingSite",
    WorkingSiteSchema
);
