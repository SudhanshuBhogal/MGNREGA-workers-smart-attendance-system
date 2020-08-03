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
    familyId: {
        type: Number,
        required: true,
    },
    jobCardId: {
        type: String,
        required: true,
        unique: true,
    },
    aadharNumber: {
        type: Number,
        required: true,
    },
    faceImage: {
        type: mongoose.Schema.Types.ObjectId,
        ref : "Image",
    },
    faceMappings: {
        type: String,
    },
    attendanceRecord: [
        {
            date: Date,
            latitude: String,
            longitude: String,
            fullAddress: String,
            city: String,
            pincode: String,
            base64img: { 
                data: Buffer, 
                contentType: String 
            },
        },
    ],
    assignedWorkingsite: {
        type: String,
    },
    previousWorkingSites: [
        {
            type : String,
        }
    ],
    contactNumber: {
            type: Number,
            unique: true,
            required: true,
    },
    date: {
        type: Date,
        default: Date.now,
    },
});


module.exports = mongoose.model(
    "Worker",
    WorkerSchema
);
