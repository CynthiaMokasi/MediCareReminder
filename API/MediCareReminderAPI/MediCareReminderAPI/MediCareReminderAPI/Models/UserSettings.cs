
using System;
using Newtonsoft.Json;

namespace MediCareReminderAPI.Models
{
    public class UserSettings
    {
        [JsonProperty("id")]
        public string Id { get; set; } = Guid.NewGuid().ToString();

        [JsonProperty("userId")]
        public string UserId { get; set; } = string.Empty;

        [JsonProperty("notificationsEnabled")]
        public bool NotificationsEnabled { get; set; } = true;

        [JsonProperty("darkMode")]
        public bool DarkMode { get; set; } = false;

        [JsonProperty("language")]
        public string Language { get; set; } = "en";

        [JsonProperty("offlineSyncEnabled")]
        public bool OfflineSyncEnabled { get; set; } = true;
    }
}

