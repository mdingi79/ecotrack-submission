const mongoose = require('mongoose');

// WasteLogs: id, category_id, item_type, weight_kg, date_logged (+ userId, collectionDate, impactScore)
const wasteLogSchema = new mongoose.Schema(
  {
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    categoryId: { type: mongoose.Schema.Types.ObjectId, ref: 'WasteCategory', required: true },
    itemType: { type: String, required: true, trim: true },
    weightKg: { type: Number, required: true, min: 0 },
    dateLogged: { type: Date, required: true, default: Date.now },
    // when the item is expected to be collected/dropped off - drives the Impact Score
    collectionDate: { type: Date, required: true },
    // computed and stored at write time so the dashboard/history don't need to recompute
    impactScore: { type: String, enum: ['High', 'Medium', 'Low'], required: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model('WasteLog', wasteLogSchema);
