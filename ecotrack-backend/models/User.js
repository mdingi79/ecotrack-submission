const mongoose = require('mongoose');

// Users: id, email, hashed_password, name (matches Part 1 design doc)
const userSchema = new mongoose.Schema(
  {
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    hashedPassword: { type: String, required: true }, // bcrypt hash only - never store plaintext
    name: { type: String, required: true, trim: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model('User', userSchema);
