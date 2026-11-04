
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.Cosmos;
using MediCareReminderAPI.Models;

namespace MediCareReminderAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class SettingsController : ControllerBase
    {
        private readonly Container _container;

        public SettingsController(
            CosmosClient cosmosClient,
            IConfiguration configuration)
        {
            var databaseName =
                configuration["CosmosDb:DatabaseName"];

            var containerName =
                configuration["CosmosDb:ContainerName"];

            if (string.IsNullOrWhiteSpace(databaseName) ||
                string.IsNullOrWhiteSpace(containerName))
            {
                throw new InvalidOperationException(
                    "CosmosDb database or container name is missing.");
            }

            _container = cosmosClient
                .GetContainer(databaseName, containerName);
        }

        // GET: api/Settings/{userId}
        [HttpGet("{userId}")]
        public async Task<IActionResult> GetSettings(string userId)
        {
            if (string.IsNullOrWhiteSpace(userId))
            {
                return BadRequest("User ID is required.");
            }

            try
            {
                var response = await _container.ReadItemAsync<UserSettings>(
                    userId,
                    new PartitionKey(userId));

                return Ok(response.Resource);
            }
            catch (CosmosException ex)
                when (ex.StatusCode == System.Net.HttpStatusCode.NotFound)
            {
                return NotFound(
                    "Settings were not found for this user.");
            }
        }

        // PUT: api/Settings/{userId}
        [HttpPut("{userId}")]
        public async Task<IActionResult> SaveSettings(
            string userId,
            [FromBody] UserSettings settings)
        {
            if (string.IsNullOrWhiteSpace(userId))
            {
                return BadRequest("User ID is required.");
            }

            if (settings == null)
            {
                return BadRequest("Settings data is required.");
            }

            if (settings.Language != "en" &&
                settings.Language != "zu" &&
                settings.Language != "st")
            {
                return BadRequest(
                    "Language must be en, zu, or st.");
            }

            settings.Id = userId;
            settings.UserId = userId;

            var response = await _container.UpsertItemAsync(
                settings,
                new PartitionKey(userId));

            return Ok(response.Resource);
        }
    }
}
