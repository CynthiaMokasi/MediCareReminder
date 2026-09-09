namespace MediCareReminderAPI.Models
{
    public class Medication
    {
        public int MedicationId { get; set; }

        public int UserId { get; set; }

        public string Name { get; set; } = string.Empty;

        public string Dosage { get; set; } = string.Empty;

        public string Frequency { get; set; } = string.Empty;

        public string ReminderTime { get; set; } = string.Empty;

        public User? User { get; set; }
    }
}