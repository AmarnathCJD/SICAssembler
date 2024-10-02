<h1>SIC Assembler (Pass 1 + Pass 2) Android App</h1>

<h2>Project Overview</h2>
<p>Welcome to the SIC Assembler Android app 🤖 — a comprehensive tool that brings the full assembly process for SIC
    programs to your mobile device. This app covers both Pass 1 and Pass 2 functionalities, making it an essential
    companion for students, developers, and enthusiasts who want to assemble SIC code with ease and efficiency.</p>

<h2>Key Features</h2>
<ul>
    <li>Preset Support: Quickly load and assemble common SIC program examples for fast learning 📚.</li>
    <li>Open Code & OPTAB from File: Flexibly work with your own custom SIC programs and instruction sets 📁.</li>
    <li>Step-by-Step Assembly: Run the assembly process in Pass 1 & Pass 2 modes, providing detailed intermediate
        results and insights 🔍.</li>
    <li>Material Design UI: A modern, intuitive interface powered by a sleek material design 🎨.</li>
    <li>Dark Mode Support: Designed for comfort in low-light environments, perfect for late-night coding sessions 🌙.
    </li>
    <li>Fast & Efficient: Optimized to assemble programs quickly, ensuring a smooth user experience 🚀.</li>
    <li>Error Handling: Comprehensive error detection and reporting to guide you through issues during assembly 🚨.</li>
    <li>Pure Kotlin: Built entirely in Kotlin, leveraging the language's power for a responsive and efficient app 💻.
    </li>
</ul>

<h2>Technology Stack</h2>
<ul>
    <li>Platform: Android</li>
    <li>Backend: Kotlin</li>
    <li>Frontend: Jetpack Compose (Kotlin)</li>
</ul>

<h2>ScreenShots</h2>

| ![Image 1](https://envs.sh/WGF.png) | ![Image 2](https://envs.sh/WGt.png) |
|:-----------------------------------:|:----------------------------------:|



<h2>How To Use</h2>

<ol>
  <li><strong>Input Source Code:</strong> 
    <p>Start by entering the SIC assembly source code into the input field provided. Ensure that the code follows standard SIC syntax and structure, including labels, instructions, and operands.</p>
  </li>
  
  <li><strong>Load OPTAB (Operation Table):</strong> 
    <p>Load the OPTAB, which contains the mnemonic operations and their corresponding opcode values. The OPTAB is preloaded with the necessary data, but you can also edit or add new instructions if required.</p>
  </li>
  
  <li><strong>Click "Assemble" Button:</strong> 
    <p>After entering the source code and loading the OPTAB, click on the "Assemble" button to begin the Pass 2 assembly process. The assembler will:</p>
    <ul>
      <li>Convert mnemonics into object code using the OPTAB.</li>
      <li>Resolve addresses using the symbol table generated in Pass 1.</li>
      <li>Generate the final object code, ready for execution.</li>
    </ul>
  </li>
  
  <li><strong>View Output:</strong> 
    <p>Once the assembly process is complete, the assembled object code will be displayed along with any relevant listings or error messages.</p>
  </li>
</ol>


<h2>Project Structure</h2>
<p>This project is designed with clean architecture principles, ensuring a clear separation of concerns. The frontend is
    built with Jetpack Compose for a modern, declarative UI, while the backend uses Kotlin to handle logic, making
    development and debugging simpler.</p>

<h2>Getting Started</h2>
<p>Ready to dive into the SIC Assembler app? Follow these steps to get up and running:</p>
<ol>
    <li>Visit the <strong>Releases</strong> section of the GitHub repository and download the latest APK file.</li>
    <li>Install the APK on your Android device.</li>
    <li>For development, clone the repository and open it in Android Studio.</li>
    <li>Run the app using an emulator or a physical device.</li>
</ol>

<h2>Contributions</h2>
<p>We welcome contributions! Whether you're interested in adding new features, fixing bugs, or improving documentation,
    feel free to fork the repository and submit a pull request. Your contributions will help make this project even
    better 👍.</p>

<h2>License</h2>
<p>This project is licensed under the <strong><a href="LICENSE">MIT License</a></strong>. Feel free to use, modify, and
    distribute this code for personal or commercial projects.</p>
