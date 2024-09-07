function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function main() {
    console.log("Sleeping for 5 seconds...");
    await sleep(5000);
    console.log("Awake now!");
}

main();
