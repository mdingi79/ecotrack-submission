const express = require('express');
const { requireAuth } = require('../middleware/auth');
const WasteCategory = require('../models/WasteCategory');

const router = express.Router();
router.use(requireAuth);

// GET /api/categories - list this user's waste categories
router.get('/', async (req, res) => {
  try {
    const categories = await WasteCategory.find({ userId: req.userId }).sort({ categoryName: 1 });
    return res.json(categories);
  } catch (err) {
    console.error('List categories error:', err.message);
    return res.status(500).json({ error: 'Could not load categories' });
  }
});

// POST /api/categories { categoryName, co2Factor, materialType }
router.post('/', async (req, res) => {
  try {
    const { categoryName, co2Factor, materialType } = req.body || {};

    if (!categoryName || !categoryName.trim()) {
      return res.status(400).json({ error: 'categoryName is required' });
    }
    if (typeof co2Factor !== 'number' || co2Factor < 0) {
      return res.status(400).json({ error: 'co2Factor must be a non-negative number' });
    }
    if (materialType && !['hazardous', 'recyclable', 'organic'].includes(materialType)) {
      return res.status(400).json({ error: 'materialType must be hazardous, recyclable or organic' });
    }

    const category = await WasteCategory.create({
      userId: req.userId,
      categoryName: categoryName.trim(),
      co2Factor,
      materialType: materialType || 'recyclable',
    });

    return res.status(201).json(category);
  } catch (err) {
    console.error('Create category error:', err.message);
    return res.status(500).json({ error: 'Could not create category' });
  }
});

module.exports = router;
