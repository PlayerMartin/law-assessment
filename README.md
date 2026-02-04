# Legal Document Conflict Analyzer

A Spring Boot application that uses AI (Google Gemini) to automatically detect contradictions and conflicts between legal documents. Upload two PDF documents, and the system will analyze them to identify any inconsistencies in obligations, rights, definitions, or applicability.

## Features

- **PDF Document Upload**: Upload two legal documents in PDF format
- **AI-Powered Analysis**: Uses Google Gemini LLM to detect contradictions
- **Interactive UI**: Side-by-side document view with highlighted conflicts
- **Conflict Navigation**: Click on detected conflicts to jump to relevant sections in both documents
- **Robust Error Handling**: Validates LLM responses and handles edge cases gracefully

## Prerequisites

- **Java 21** or higher
- **Gradle** (wrapper included, no installation needed)
- **Google Gemini API Key** ([Get one here](https://ai.google.dev/))

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd assessment
```

### 2. Configure Application Properties

**Important:** You must create an `application.properties` file before running the application.

1. Navigate to `src/main/resources/`
2. Copy the example file:
   ```bash
   cp application.properties.example application.properties
   ```
   Or on Windows:
   ```cmd
   copy application.properties.example application.properties
   ```
3. Edit `application.properties` and replace `YOUR_GEMINI_API_KEY` with your actual Gemini API key:
   ```properties
   gemini-api-key=your_actual_api_key_here
   ```

### 3. Run the Application

**Windows:**
```cmd
.\gradlew.bat bootRun
```

**Mac/Linux:**
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## How to Use

1. Open your browser and navigate to `http://localhost:8080`
2. Upload two PDF documents containing legal text
3. Click **"Analyze Conflicts"**
4. Wait for the AI analysis to complete
5. Review the detected contradictions in the sidebar
6. Click on any conflict card to highlight and scroll to the relevant sections in both documents

## Project Structure

```
src/main/java/com/example/assessment/
├── controller/
│   └── ApiController.java          # Handles HTTP requests and renders UI
├── service/
│   └── LlmService.java             # Manages LLM API calls and PDF processing
├── utils/
│   ├── Result/                     # Result pattern
│   └── Validation/                 # Validates LLM response structure
└── AssessmentApplication.java      # Main Spring Boot entry point

src/main/resources/
├── templates/
│   └── index.html                  # Thymeleaf template with interactive UI
├── prompt/
│   └── conflict-prompt.txt         # LLM system prompt with instructions
└── application.properties.example  # Configuration template
```

## API Configuration

The application uses Google Gemini 2.5 Flash model by default. You can modify the model endpoint in `application.properties`:

```properties
gemini-model-api=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent
```
