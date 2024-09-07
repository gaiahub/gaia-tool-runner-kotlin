shell="/bin/sh"

command=$(cat deploy.gaia)
$shell -c eval "$command"