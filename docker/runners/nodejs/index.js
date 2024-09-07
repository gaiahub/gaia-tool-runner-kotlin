console.log('new script');
function wait(ms) {
  const start = Date.now();
  while (Date.now() - start < ms) {
    // Busy-wait loop
  }
}

wait(5000)
console.log('woke up  ##');