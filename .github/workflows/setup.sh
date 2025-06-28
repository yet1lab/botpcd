#==============================================
#     Add Docker's official GPG key
#==============================================
sudo apt update
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL \
	https://download.docker.com/linux/ubuntu/gpg \
	-o /etc/apt/keyrings/docker.asc

sudo chmod a+r /etc/apt/keyrings/docker.asc
#==============================================
#    Add the repository to Apt sources
#==============================================
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

#==============================================
#               Install docker
#==============================================
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

#==============================================
#     Certbot and https
#==============================================
sudo apt install certbot python-certbot-nginx
sudo apt search nginx
sudo apt install nginx

#==============================================
#       Automated the setup of the github actions
#==============================================
# Create a folder
mkdir actions-runner && cd actions-runnerCopied!
curl -o actions-runner-linux-x64-2.324.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.324.0/actions-runner-linux-x64-2.324.0.tar.gz
echo "e8e24a3477da17040b4d6fa6d34c6ecb9a2879e800aa532518ec21e49e21d7b4  actions-runner-linux-x64-2.324.0.tar.gz" | shasum -a 256 -c
tar xzf ./actions-runner-linux-x64-2.324.0.tar.gz




