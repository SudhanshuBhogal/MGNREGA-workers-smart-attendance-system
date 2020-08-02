var mongoose = require('mongoose'); 

var workDaySchema = new mongoose.Schema({ 
    date: {
        type: String,
        unique: true,
    },
    presentWorkers: [
        {
            type : mongoose.Schema.Types.ObjectId,
            ref : "Worker",
        }
    ]
}); 

module.exports = new mongoose.model('Workday', workDaySchema); 
