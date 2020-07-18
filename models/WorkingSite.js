const mongoose = require("mongoose");

const WorkingSiteSchema = new mongoose.Schema({
    id: {
        type: String,
        required: true,
        unique: true,
    },
    name: {
        type: String,
        required: true,
        unique: true,
    },
    addressLine: {
        type: String,
        required: true,
    },
    state: {
        type: String,
        required: true,
    },
    district: {
        type: String,
        required: true,
    },
    block: {
        type: String,
        required: true,
    },
    panchayat: {
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
    status: {
        type: String,
        default: "Not commenced",
    },
    estimatedCost: {
        type: Number,
    },
    estimatedCompletionTime: {
        type: Number,
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
