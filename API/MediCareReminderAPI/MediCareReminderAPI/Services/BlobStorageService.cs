
using Azure.Storage.Blobs;
using Azure.Storage.Blobs.Models;

namespace MediCareReminderAPI.Services
{
    public class BlobStorageService
    {
        private readonly BlobContainerClient _containerClient;

        public BlobStorageService(IConfiguration configuration)
        {
            string? connectionString =
                configuration["AzureBlobStorage:ConnectionString"];

            string containerName =
                configuration["AzureBlobStorage:ContainerName"]
                ?? "medicare-files";

            if (string.IsNullOrWhiteSpace(connectionString))
            {
                throw new InvalidOperationException(
                    "Azure Blob Storage connection string is not configured.");
            }

            var blobServiceClient =
                new BlobServiceClient(connectionString);

            _containerClient =
                blobServiceClient.GetBlobContainerClient(containerName);
        }

        public async Task<string> UploadFileAsync(
            Stream fileStream,
            string fileName,
            string contentType)
        {
            if (string.IsNullOrWhiteSpace(fileName))
            {
                throw new ArgumentException("A file name is required.");
            }

            string safeFileName = Path.GetFileName(fileName);

            string blobName =
                $"{Guid.NewGuid()}-{safeFileName}";

            BlobClient blobClient =
                _containerClient.GetBlobClient(blobName);

            await blobClient.UploadAsync(
                fileStream,
                new BlobUploadOptions
                {
                    HttpHeaders = new BlobHttpHeaders
                    {
                        ContentType = string.IsNullOrWhiteSpace(contentType)
                            ? "application/octet-stream"
                            : contentType
                    }
                });

            return blobName;
        }
    }
}
