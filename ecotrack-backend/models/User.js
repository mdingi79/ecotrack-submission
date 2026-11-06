const mongoose = require('mongoose');

// Users: email, hashed_password, name, optional Google account identifier.
const userSchema = new mongoose.Schema(
  {
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    hashedPassword: { type: String, required: true },
    name: { type: String, required: true, trim: true },
    googleId: { type: String, unique: true, sparse: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model('User', userSchema);
