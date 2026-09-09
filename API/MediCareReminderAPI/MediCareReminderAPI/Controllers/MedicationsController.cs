using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MediCareReminderAPI.Data;
using MediCareReminderAPI.Models;

namespace MediCareReminderAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class MedicationsController : ControllerBase
    {
        private readonly ApplicationDbContext _context;

        public MedicationsController(ApplicationDbContext context)
        {
            _context = context;
        }

        [HttpGet("{userId}")]
        public async Task<IActionResult> GetMedications(int userId)
        {
            var medications = await _context.Medications
                .Where(m => m.UserId == userId)
                .ToListAsync();

            return Ok(medications);
        }

        [HttpPost]
        public async Task<IActionResult> AddMedication(Medication medication)
        {
            var user = await _context.Users.FindAsync(medication.UserId);

            if (user == null)
                return BadRequest("User not found.");

            medication.MedicationId = 0;

            _context.Medications.Add(medication);

            await _context.SaveChangesAsync();

            return Ok(new
            {
                medicationId = medication.MedicationId,
                userId = medication.UserId,
                name = medication.Name,
                dosage = medication.Dosage,
                frequency = medication.Frequency,
                reminderTime = medication.ReminderTime
            });
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateMedication(
            int id,
            Medication medication)
        {
            var existing = await _context.Medications.FindAsync(id);

            if (existing == null)
                return NotFound("Medication not found.");

            existing.Name = medication.Name;
            existing.Dosage = medication.Dosage;
            existing.Frequency = medication.Frequency;
            existing.ReminderTime = medication.ReminderTime;

            await _context.SaveChangesAsync();

            return Ok(existing);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteMedication(int id)
        {
            var medication = await _context.Medications.FindAsync(id);

            if (medication == null)
                return NotFound("Medication not found.");

            _context.Medications.Remove(medication);

            await _context.SaveChangesAsync();

            return Ok("Medication deleted successfully.");
        }
    }
}