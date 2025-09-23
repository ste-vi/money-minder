
# Build the Docker image
docker build -t moneyminder-native-builder -f Dockerfile.native .

# Create a volume to extract the binary
docker volume create moneyminder-native-output

# Run the container to extract the binary
docker run --rm -v moneyminder-native-output:/output moneyminder-native-builder

# Copy the binary from the Docker volume to the local filesystem
$tempContainer = docker create -v moneyminder-native-output:/output alpine
docker cp ${tempContainer}:/output/moneyminder ./money-minder-amazonlinux
docker rm $tempContainer

Write-Host "Native executable extracted to ./moneyminder-amazonlinux"
Write-Host "You can now copy this file to your Amazon Linux 2023 server using scp"
