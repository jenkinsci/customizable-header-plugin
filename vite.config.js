import { defineConfig } from "vite";
import eslint from 'vite-plugin-eslint';

export default defineConfig({
  base: "./",
  build: {
    sourcemap: true,
    rollupOptions: {
      input: {
        main: "src/main/js/index.js",
      },
      output: {
        entryFileNames: "custom-header.js",
        dir: "src/main/webapp/js/bundles",
      },
    },
  },
  plugins: [
    eslint({
      cache: false,
      fix: true,
      include: ['src/main/js/**/*.js'],
      exclude: ['node_modules', 'target'],
    }),
  ],
});
