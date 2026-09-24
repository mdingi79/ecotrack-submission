const mongoose = require('mongoose');

// WasteCategories: id, user_id, category_name, co2_factor (kg CO2 saved per kg recycled)
const wasteCategorySchema = new mongoose.Schema(
  {
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    categoryName: { type: String, required: true, trim: true },
    // kg of CO2 saved per kg of this material recycled - used by the dashboard
    co2Factor: { type: Number, required: true, min: 0 },
    // used by the Impact Score engine to flag hazardous / recyclable / organic materials
    materialType: {
      type: String,
      enum: ['hazardous', 'recyclable', 'organic'],
      default: 'recyclable',
    },
  },
  { timestamps: true }
);

module.exports = mongoose.model('WasteCategory', wasteCategorySchema);
