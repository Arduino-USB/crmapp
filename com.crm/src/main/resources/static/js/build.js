const esbuild = require("esbuild");
const fs = require("fs");
const path = require("path");

const srcDir = "./src";

const entryPoints = fs.readdirSync(srcDir)
	.filter(file => file.endsWith(".js"))
	.map(file => path.join(srcDir, file));

if (entryPoints.length === 0) {
	console.log("No JavaScript files found in src/");
	process.exit(0);
}

console.log("Building:");
entryPoints.forEach(file => console.log(" - " + file));

esbuild.build({
	entryPoints: entryPoints,

	bundle: true,

	format: "esm",

	outdir: ".",

	platform: "browser",

	sourcemap: true,

	entryNames: "[name]"
})
.then(() => {
	console.log("Build complete");
})
.catch(() => process.exit(1));
