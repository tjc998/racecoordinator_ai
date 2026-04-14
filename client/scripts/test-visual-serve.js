const { execSync } = require("child_process");
const fs = require("fs");
const path = require("path");

const distPath = path.join(__dirname, "../dist/client");

if (!fs.existsSync(distPath)) {
  console.log("Build output not found. Running ng build...");
  try {
    execSync("npm run build", { stdio: "inherit" });
  } catch (e) {
    console.error("Failed to run ng build", e);
    process.exit(1);
  }
}

console.log("Starting sirv...");
try {
  // Use npx to ensure sirv is available even if not globally installed, though we added it to devDependencies
  execSync("npx sirv dist/client --port 4200 --host 0.0.0.0 --single", {
    stdio: "inherit",
  });
} catch (e) {
  console.error("Failed to start sirv", e);
  process.exit(1);
}
