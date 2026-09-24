const { calculateImpactScore } = require('../utils/impactScore');

describe('calculateImpactScore', () => {
  const now = new Date('2026-09-23T12:00:00Z');

  test('hazardous material is always High, even with a far-off collection', () => {
    const farAway = new Date('2026-10-15T12:00:00Z'); // 22 days out
    expect(calculateImpactScore('hazardous', now, farAway)).toBe('High');
  });

  test('any material becomes High when collection is under 24 hours away', () => {
    const soon = new Date('2026-09-24T08:00:00Z'); // 20 hours out
    expect(calculateImpactScore('organic', now, soon)).toBe('High');
  });

  test('recyclable material is Medium when collection is more than 24h but still within 3 days', () => {
    const inTwoDays = new Date('2026-09-25T12:00:00Z');
    expect(calculateImpactScore('recyclable', now, inTwoDays)).toBe('Medium');
  });

  test('organic material with a collection between 3 and 7 days out is Medium (deadline-driven)', () => {
    const inFourDays = new Date('2026-09-27T12:00:00Z');
    expect(calculateImpactScore('organic', now, inFourDays)).toBe('Medium');
  });

  test('organic material with a drop-off more than 7 days away is Low', () => {
    const inTenDays = new Date('2026-10-03T12:00:00Z');
    expect(calculateImpactScore('organic', now, inTenDays)).toBe('Low');
  });

  test('throws on an invalid date', () => {
    expect(() => calculateImpactScore('organic', now, 'not-a-date')).toThrow();
  });
});
