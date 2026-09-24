/**
 * Impact Score engine.
 *
 * Rules (from Part 1 design doc):
 *  - High:   hazardous material OR collection is less than 24 hours away
 *  - Medium: recyclable material OR collection is less than 3 days away
 *  - Low:    non-urgent/organic material OR drop-off is more than 7 days away
 *
 * The two inputs can disagree (e.g. an organic item with a collection due in 2 hours);
 * urgency (the deadline) is always allowed to escalate the score, but a calmer deadline
 * never downgrades a hazardous item below High. Concretely: check High conditions first,
 * then Medium, and only fall through to Low if neither material nor deadline pushes it up.
 *
 * @param {('hazardous'|'recyclable'|'organic')} materialType
 * @param {Date|string|number} now - current time
 * @param {Date|string|number} collectionDate - when the item will be collected/dropped off
 * @returns {'High'|'Medium'|'Low'}
 */
function calculateImpactScore(materialType, now, collectionDate) {
  const nowMs = new Date(now).getTime();
  const collectionMs = new Date(collectionDate).getTime();

  if (Number.isNaN(nowMs) || Number.isNaN(collectionMs)) {
    throw new Error('Invalid date supplied to calculateImpactScore');
  }

  const hoursUntilCollection = (collectionMs - nowMs) / (1000 * 60 * 60);
  const daysUntilCollection = hoursUntilCollection / 24;

  const isHazardous = materialType === 'hazardous';
  const isRecyclable = materialType === 'recyclable';

  // High: hazardous material OR less than 24h away
  if (isHazardous || hoursUntilCollection < 24) {
    return 'High';
  }

  // Medium: recyclable material OR less than 3 days away
  if (isRecyclable || daysUntilCollection < 3) {
    return 'Medium';
  }

  // Low: organic / non-urgent, or more than 7 days out
  return 'Low';
}

module.exports = { calculateImpactScore };
