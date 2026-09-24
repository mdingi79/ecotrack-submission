const express = require('express');
const { requireAuth } = require('../middleware/auth');
const WasteLog = require('../models/WasteLog');
const WasteCategory = require('../models/WasteCategory');
const { calculateImpactScore } = require('../utils/impactScore');

const router = express.Router();
router.use(requireAuth);

// POST /api/logs { itemType, weightKg, categoryId, dateLogged, collectionDate }
// -> { logId, itemType, weightKg, impactScore }
router.post('/', async (req, res) => {
  try {
    const { itemType, weightKg, categoryId, dateLogged, collectionDate } = req.body || {};

    if (!itemType || !itemType.trim()) {
      return res.status(400).json({ error: 'itemType is required' });
    }
    if (typeof weightKg !== 'number' || Number.isNaN(weightKg) || weightKg <= 0) {
      return res.status(400).json({ error: 'weightKg must be a positive number' });
    }
    if (!categoryId) {
      return res.status(400).json({ error: 'categoryId is required' });
    }
    if (!collectionDate || Number.isNaN(new Date(collectionDate).getTime())) {
      return res.status(400).json({ error: 'collectionDate must be a valid date' });
    }

    const category = await WasteCategory.findOne({ _id: categoryId, userId: req.userId });
    if (!category) {
      return res.status(404).json({ error: 'Category not found' });
    }

    const loggedAt = dateLogged ? new Date(dateLogged) : new Date();
    const impactScore = calculateImpactScore(category.materialType, loggedAt, collectionDate);

    const log = await WasteLog.create({
      userId: req.userId,
      categoryId,
      itemType: itemType.trim(),
      weightKg,
      dateLogged: loggedAt,
      collectionDate,
      impactScore,
    });

    return res.status(201).json({
      logId: log._id,
      itemType: log.itemType,
      weightKg: log.weightKg,
      impactScore: log.impactScore,
    });
  } catch (err) {
    console.error('Create log error:', err.message);
    return res.status(500).json({ error: 'Could not create waste log' });
  }
});

// GET /api/logs - all logs for the logged-in user, most recent first
router.get('/', async (req, res) => {
  try {
    const logs = await WasteLog.find({ userId: req.userId })
      .populate('categoryId', 'categoryName co2Factor materialType')
      .sort({ dateLogged: -1 });
    return res.json(logs);
  } catch (err) {
    console.error('List logs error:', err.message);
    return res.status(500).json({ error: 'Could not load waste logs' });
  }
});

// GET /api/logs/:id
router.get('/:id', async (req, res) => {
  try {
    const log = await WasteLog.findOne({ _id: req.params.id, userId: req.userId }).populate(
      'categoryId',
      'categoryName co2Factor materialType'
    );
    if (!log) return res.status(404).json({ error: 'Log not found' });
    return res.json(log);
  } catch (err) {
    console.error('Get log error:', err.message);
    return res.status(500).json({ error: 'Could not load waste log' });
  }
});

// PUT /api/logs/:id - edit an existing log (re-runs the impact score calculation)
router.put('/:id', async (req, res) => {
  try {
    const log = await WasteLog.findOne({ _id: req.params.id, userId: req.userId });
    if (!log) return res.status(404).json({ error: 'Log not found' });

    const { itemType, weightKg, categoryId, collectionDate } = req.body || {};

    if (itemType !== undefined) log.itemType = itemType.trim();
    if (weightKg !== undefined) {
      if (typeof weightKg !== 'number' || weightKg <= 0) {
        return res.status(400).json({ error: 'weightKg must be a positive number' });
      }
      log.weightKg = weightKg;
    }
    if (categoryId !== undefined) log.categoryId = categoryId;
    if (collectionDate !== undefined) log.collectionDate = collectionDate;

    const category = await WasteCategory.findOne({ _id: log.categoryId, userId: req.userId });
    if (!category) return res.status(404).json({ error: 'Category not found' });

    log.impactScore = calculateImpactScore(category.materialType, log.dateLogged, log.collectionDate);
    await log.save();

    return res.json(log);
  } catch (err) {
    console.error('Update log error:', err.message);
    return res.status(500).json({ error: 'Could not update waste log' });
  }
});

// DELETE /api/logs/:id
router.delete('/:id', async (req, res) => {
  try {
    const result = await WasteLog.deleteOne({ _id: req.params.id, userId: req.userId });
    if (result.deletedCount === 0) return res.status(404).json({ error: 'Log not found' });
    return res.status(204).send();
  } catch (err) {
    console.error('Delete log error:', err.message);
    return res.status(500).json({ error: 'Could not delete waste log' });
  }
});

// GET /api/logs/summary/dashboard - totals for the Sustainability Dashboard
router.get('/summary/dashboard', async (req, res) => {
  try {
    const logs = await WasteLog.find({ userId: req.userId }).populate('categoryId', 'co2Factor');

    const totalWeightKg = logs.reduce((sum, l) => sum + l.weightKg, 0);
    const totalCo2SavedKg = logs.reduce((sum, l) => {
      const factor = l.categoryId?.co2Factor || 0;
      return sum + l.weightKg * factor;
    }, 0);

    const now = new Date();
    const upcomingCollections = logs
      .filter((l) => new Date(l.collectionDate) > now)
      .sort((a, b) => new Date(a.collectionDate) - new Date(b.collectionDate))
      .slice(0, 5)
      .map((l) => ({ logId: l._id, itemType: l.itemType, collectionDate: l.collectionDate, impactScore: l.impactScore }));

    return res.json({
      totalWeightKg: Number(totalWeightKg.toFixed(2)),
      totalCo2SavedKg: Number(totalCo2SavedKg.toFixed(2)),
      upcomingCollections,
    });
  } catch (err) {
    console.error('Dashboard summary error:', err.message);
    return res.status(500).json({ error: 'Could not load dashboard summary' });
  }
});

module.exports = router;
