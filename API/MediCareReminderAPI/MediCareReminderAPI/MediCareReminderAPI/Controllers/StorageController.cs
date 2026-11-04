
using MediCareReminderAPI.Services;
using Microsoft.AspNetCore.Mvc;

namespace MediCareReminderAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class StorageController : ControllerBase
    {
        private readonly BlobStorageService _blobStorageService;

        public StorageController(BlobStorageService blobStorageService)
        {
            _blobStorageService = blobStorageService;
        }

        [HttpPost("upload")]
        [RequestSizeLimit(10 * 1024 * 1024)]
        public async Task<IActionResult> Upload(IFormFile? file)
        {
            if (file == null || file.Length == 0)
            {
                return BadRequest(new
                {
                    message = "Please select a file to upload."
                });
            }

            if (file.Length > 10 * 1024 * 1024)
            {
                return BadRequest(new
                {
                    message = "The file must be 10 MB or smaller."
                });
            }

            string extension = Path.GetExtension(file.FileName).ToLowerInvariant();

            string[] allowedExtensions =
                { ".jpg", ".jpeg", ".png", ".pdf" };

            if (!allowedExtensions.Contains(extension))
            {
                return BadRequest(new
                {
                    message = "Only JPG, PNG and PDF files are allowed."
                });
            }

            try
            {
                await using var stream = file.OpenReadStream();

                string blobName =
                    await _blobStorageService.UploadFileAsync(
                        stream,
                        file.FileName,
                        file.ContentType);

                return Ok(new
                {
                    message = "File uploaded successfully.",
                    fileName = Path.GetFileName(file.FileName),
                    blobName = blobName
                });
            }
            catch (Exception)
            {
                return StatusCode(500, new
                {
                    message = "The file could not be uploaded. Check the API storage configuration."
                });
            }
        }
    }
}
