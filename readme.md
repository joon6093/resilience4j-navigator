# Resilience4j Navigator

## Summary

An IntelliJ IDEA plugin that enables direct navigation between **Resilience4j annotations** and their **corresponding implementation methods**.

This tool reduces friction for developers working with Resilience4j in Spring-based applications, making it easier to trace resilience mechanisms across annotation usage and business logic.

## Goals

- Provide seamless navigation between annotations and their implementations.
- Reduce wasted time on manual searches.
- Support teams working with Resilience4j at scale.

## Non-Goals

- Not a runtime monitoring or management tool.
- Not designed to replace observability dashboards.
- Not a general-purpose navigation plugin for all Java annotations.

## Motivation

When using Resilience4j annotations, developers often ask:

- *“Where is this fallback method defined?”*
- *“Is this method actually used or safe to delete?”*
- *“Which annotations reference this method?”*

This plugin exists to answer those questions directly,  
making the hidden connections between annotations and implementations explicit,  
and enabling developers to work with resilience patterns more confidently.

## Development Status

This plugin is **still under active development** and has **not yet been officially released**.

### Try It Out

If you’d like to experiment with it early, you can build and install the plugin manually:

1. **Clone the repository**

   ```
   git clone https://github.com/joon6093/resilience4j-navigator.git
   cd resilience4j-navigator
   ```

2. **Build the plugin**

   ```
   ./gradlew buildPlugin
   ```

   After the build completes, you’ll find the packaged ZIP file in:

   ```
   build/distributions/resilience4j-navigator-0.1.0.zip
   ```

3. **Install into IntelliJ IDEA**
    - Open IntelliJ IDEA
    - Go to `Settings → Plugins → ⚙ → Install Plugin from Disk`
    - Select the ZIP file
    - Restart IntelliJ
