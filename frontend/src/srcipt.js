/**
 * API & Socket.IO Tester Script v2
 * Handles UI interactions, API requests, Socket.IO connections,
 * request management, persistence via localStorage (including responses),
 * and export/import.
 */
document.addEventListener('DOMContentLoaded', () => {
  console.log("Tester DOM Loaded. Initializing v2...");

  // --- Globals ---
  let socket = null;
  let lastResponse = null; // Holds the LIVE response details from the last fetch
  let loadedRequestId = null;
  const STORAGE_KEY = 'apiSocketTesterData_v2';

  // Central data object for storage - Initialized by loadDataFromStorage
  let appData = {
    savedRequests: [], // Now requests can contain a 'lastResponse' property
    socketSettings: {
      url: '',
      connectionHeaders: ''
    },
    scratchpadContent: ''
  };

  // --- DOM Element References ---
  // (Verify these IDs match index.html exactly)
  const exportDataBtn = document.getElementById('export-data-btn');
  const importFileEl = document.getElementById('import-file-input');
  const importDataBtn = document.getElementById('import-data-btn');
  const clearAllDataBtn = document.getElementById('clear-all-data-btn');
  const requestNameInput = document.getElementById('request-name-input');
  const saveRequestBtn = document.getElementById('save-request-btn');
  const requestListSelect = document.getElementById('request-list-select');
  const updateRequestBtn = document.getElementById('update-request-btn');
  const deleteRequestBtn = document.getElementById('delete-request-btn');
  const loadedRequestIdInput = document.getElementById('loaded-request-id-input');
  const apiMethodSelect = document.getElementById('api-method-select');
  const apiUrlInput = document.getElementById('api-url-input');
  const apiHeadersTextarea = document.getElementById('api-headers-textarea');
  const apiBodyTextarea = document.getElementById('api-body-textarea');
  const sendApiRequestBtn = document.getElementById('send-api-request-btn');
  const apiStatusEl = document.getElementById('api-status-el');
  const apiResponseHeadersEl = document.getElementById('api-response-headers-el');
  const apiResponseBodyEl = document.getElementById('api-response-body-el');
  const apiTestScriptTextarea = document.getElementById('api-test-script-textarea');
  const runApiTestsBtn = document.getElementById('run-api-tests-btn');
  const apiTestResultsEl = document.getElementById('api-test-results-el');
  const socketUrlInput = document.getElementById('socket-url-input');
  const socketConnHeadersTextarea = document.getElementById('socket-conn-headers-textarea');
  const socketConnectBtn = document.getElementById('socket-connect-btn');
  const socketDisconnectBtn = document.getElementById('socket-disconnect-btn');
  const socketStatusEl = document.getElementById('socket-status-el');
  const socketEventNameInput = document.getElementById('socket-event-name-input');
  const socketEventDataTextarea = document.getElementById('socket-event-data-textarea');
  const socketEmitBtn = document.getElementById('socket-emit-btn');
  const socketLogAreaEl = document.getElementById('socket-log-area-el');
  const socketClearLogBtn = document.getElementById('socket-clear-log-btn');
  const scratchpadTextarea = document.getElementById('scratchpad-textarea');

  // --- Utility Functions ---
  function generateId() {
    /* ... */
    return Date.now().toString(36) + Math.random().toString(36).substring(2, 9);
  }

  function parseHeaders(headersText) {
    /* ... */
    const headers = {};
    if (!headersText) return headers;
    const lines = headersText.split('\n');
    lines.forEach(line => {
      const parts = line.match(/^([^:]+?)\s*:\s*(.*)$/);
      if (parts && parts.length === 3) {
        const key = parts[1].trim();
        const value = parts[2].trim();
        if (key) {
          headers[key] = value;
        }
      }
    });
    return headers;
  }

  function logSocketMessage(message) {
    /* ... */
    const timestamp = new Date().toLocaleTimeString();
    // const safeMessage = String(message).replace(/</g, "&lt;").replace(/>/g, "&gt;");
    const safeMessage = String(message);
    socketLogAreaEl.textContent += `[${timestamp}] ${safeMessage}\n`;
    socketLogAreaEl.scrollTop = socketLogAreaEl.scrollHeight;
  }

  // --- NEW: Response Display Helper ---
  /**
   * Updates the API Response UI elements based on a response object.
   * Clears the UI if no response object is provided.
   * @param {object | null} resp The response object (like global lastResponse or stored response)
   */
  function displayResponse(resp) {
    if (resp) {
      apiStatusEl.textContent = `Status: ${resp.status || '-'} ${resp.statusText || (resp.error ? '(Request Failed)' : '')}`;
      let headersStr = '';
      if (resp.headers && typeof resp.headers === 'object') {
        Object.entries(resp.headers).forEach(([key, value]) => {
          headersStr += `${key}: ${value}\n`;
        });
      }
      apiResponseHeadersEl.textContent = headersStr || '(No headers)';

      // Display body (handle potential errors/JSON structure)
      if (resp.error) {
        apiResponseBodyEl.textContent = `Request failed.\nError: ${resp.error}`;
      } else if (resp.body !== null && resp.body !== undefined) {
        if (typeof resp.body === 'object') { // Already parsed JSON
          apiResponseBodyEl.textContent = JSON.stringify(resp.body, null, 2);
        } else { // Assume text
          apiResponseBodyEl.textContent = String(resp.body);
        }
      } else {
        apiResponseBodyEl.textContent = '(Empty response body)';
      }
      // Add timestamp if available
      if (resp.timestamp) {
        const time = new Date(resp.timestamp).toLocaleString();
        apiStatusEl.textContent += ` (Saved: ${time})`;
      }

    } else {
      // Clear response UI if no response object
      apiStatusEl.textContent = 'Status: -';
      apiResponseHeadersEl.textContent = '';
      apiResponseBodyEl.textContent = '';
      // lastResponse = null; // Reset global live response tracker is handled elsewhere
    }
    // Do not clear test results here, only when sending a new request
  }


  // --- Storage Functions --- (saveDataToStorage, loadDataFromStorage, clearAllStoredData - unchanged)
  function saveDataToStorage() {
    /* ... unchanged, still saves entire appData ... */
    appData.socketSettings.url = socketUrlInput.value.trim();
    appData.socketSettings.connectionHeaders = socketConnHeadersTextarea.value.trim();
    appData.scratchpadContent = scratchpadTextarea.value;
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(appData));
    } catch (e) {
      console.error("Error saving data to localStorage:", e);
    }
  }

  function loadDataFromStorage() {
    /* ... unchanged, loads entire appData, updates UI ... */
    console.log("Loading data from localStorage...");
    const storedData = localStorage.getItem(STORAGE_KEY);
    let loadedSuccessfully = false;
    if (storedData) {
      try {
        const parsedData = JSON.parse(storedData);
        if (parsedData && typeof parsedData === 'object') {
          appData.savedRequests = Array.isArray(parsedData.savedRequests) ? parsedData.savedRequests : [];
          appData.socketSettings = (parsedData.socketSettings && typeof parsedData.socketSettings === 'object') ? parsedData.socketSettings : {
            url: '',
            connectionHeaders: ''
          };
          appData.scratchpadContent = typeof parsedData.scratchpadContent === 'string' ? parsedData.scratchpadContent : '';
          appData.socketSettings.url = appData.socketSettings.url || '';
          appData.socketSettings.connectionHeaders = appData.socketSettings.connectionHeaders || '';
          loadedSuccessfully = true;
          console.log("Data loaded:", appData);
        } else {
          console.warn("Invalid data structure found in localStorage.");
        }
      } catch (e) {
        console.error("Error parsing data from localStorage:", e);
      }
    }
    if (!loadedSuccessfully) {
      console.log("No valid data found in storage, initializing defaults.");
      appData = {
        savedRequests: [],
        socketSettings: {
          url: '',
          connectionHeaders: ''
        },
        scratchpadContent: ''
      };
    }
    socketUrlInput.value = appData.socketSettings.url;
    socketConnHeadersTextarea.value = appData.socketSettings.connectionHeaders;
    scratchpadTextarea.value = appData.scratchpadContent;
    updateRequestListUI();
    clearRequestForm(); // Start with clear form and response
  }

  function clearAllStoredData() {
    /* ... unchanged ... */
    if (confirm("Are you sure you want to permanently delete ALL saved requests, Socket.IO settings, and scratchpad content?")) {
      appData = {
        savedRequests: [],
        socketSettings: {
          url: '',
          connectionHeaders: ''
        },
        scratchpadContent: ''
      };
      loadedRequestId = null;
      try {
        localStorage.removeItem(STORAGE_KEY);
        clearRequestForm();
        updateRequestListUI();
        socketUrlInput.value = '';
        socketConnHeadersTextarea.value = '';
        scratchpadTextarea.value = '';
        alert("All stored data cleared.");
      } catch (e) {
        console.error("Error clearing localStorage:", e);
        alert("Could not clear stored data.");
      }
    }
  }

  // --- Request Management Logic ---

  function saveRequest() {
    console.log("Attempting to save request...");
    const name = requestNameInput.value.trim();
    if (!name) {
      alert('Please enter a name for the request.');
      return;
    }
    const newRequest = {
      id: generateId(),
      name: name,
      url: apiUrlInput.value.trim(),
      method: apiMethodSelect.value,
      headers: apiHeadersTextarea.value,
      body: apiBodyTextarea.value,
      testScript: apiTestScriptTextarea.value,
      lastResponse: null // Initialize with no response
    };
    appData.savedRequests.push(newRequest);
    saveDataToStorage(); // Save updated appData
    requestNameInput.value = '';
    updateRequestListUI();
    requestListSelect.value = newRequest.id;
    loadRequest(newRequest.id); // Load it back (will clear response area)
    console.log(`Request "${name}" saved.`);
  }

  function updateRequestListUI() {
    /* ... unchanged ... */
    console.log("Updating request list UI...");
    const currentSelection = requestListSelect.value;
    requestListSelect.innerHTML = '<option value="">-- Select a Request --</option>';
    appData.savedRequests.forEach(req => {
      const option = document.createElement('option');
      option.value = req.id;
      option.textContent = req.name;
      requestListSelect.appendChild(option);
    });
    if (appData.savedRequests.some(r => r.id === currentSelection)) {
      requestListSelect.value = currentSelection;
    }
    console.log(`Request list updated with ${appData.savedRequests.length} items.`);
  }

  function loadRequest(id) {
    console.log(`Attempting to load request ID: ${id}`);
    const request = appData.savedRequests.find(r => r.id === id);
    if (request) {
      console.log("Loading request:", request.name);
      requestNameInput.value = request.name;
      apiUrlInput.value = request.url;
      apiMethodSelect.value = request.method;
      apiHeadersTextarea.value = request.headers;
      apiBodyTextarea.value = request.body;
      apiTestScriptTextarea.value = request.testScript;
      loadedRequestIdInput.value = request.id;
      loadedRequestId = request.id;

      // *** NEW: Display saved response ***
      console.log("Displaying saved response for loaded request (if any):", request.lastResponse);
      displayResponse(request.lastResponse); // Use the helper function
      apiTestResultsEl.textContent = ''; // Clear test results when loading

    } else {
      console.log("No request found or placeholder selected, clearing form.");
      clearRequestForm(); // Clears form and response areas
    }
  }

  function clearRequestForm() { // Now also clears response area
    console.log("Clearing API request form and response areas.");
    requestNameInput.value = '';
    apiUrlInput.value = '';
    apiMethodSelect.value = 'GET';
    apiHeadersTextarea.value = '';
    apiBodyTextarea.value = '';
    apiTestScriptTextarea.value = '';
    loadedRequestIdInput.value = '';
    loadedRequestId = null;
    requestListSelect.value = '';
    // Clear response UI
    displayResponse(null); // Use helper to clear response
    apiTestResultsEl.textContent = ''; // Clear test results
    lastResponse = null; // Clear global live response object
  }

  function updateSelectedRequest() { // No change needed here, response saved by handleApiSend
    console.log(`Attempting to update request ID: ${loadedRequestId}`);
    if (!loadedRequestId) {
      alert('No request selected to update.');
      return;
    }
    const requestIndex = appData.savedRequests.findIndex(r => r.id === loadedRequestId);
    if (requestIndex === -1) {
      alert('Error updating: Request not found.');
      loadedRequestId = null;
      return;
    }
    const updatedName = requestNameInput.value.trim();
    if (!updatedName) {
      alert('Request name cannot be empty.');
      return;
    }
    // Update fields, keeping existing lastResponse
    appData.savedRequests[requestIndex] = {
      ...appData.savedRequests[requestIndex], // Keep ID and lastResponse
      name: updatedName,
      url: apiUrlInput.value.trim(),
      method: apiMethodSelect.value,
      headers: apiHeadersTextarea.value,
      body: apiBodyTextarea.value,
      testScript: apiTestScriptTextarea.value
    };
    saveDataToStorage(); // Persist changes
    updateRequestListUI();
    requestListSelect.value = loadedRequestId;
    console.log(`Request "${updatedName}" updated (excluding response).`);
  }

  function deleteSelectedRequest() { // No change needed here
    console.log(`Attempting to delete request ID: ${loadedRequestId}`);
    if (!loadedRequestId) {
      alert('No request selected to delete.');
      return;
    }
    const requestIndex = appData.savedRequests.findIndex(r => r.id === loadedRequestId);
    if (requestIndex === -1) {
      alert('Error deleting: Request not found.');
      loadedRequestId = null;
      return;
    }
    const deletedName = appData.savedRequests[requestIndex].name;
    if (confirm(`Are you sure you want to delete the request "${deletedName}"?`)) {
      appData.savedRequests.splice(requestIndex, 1);
      saveDataToStorage(); // Persist changes
      clearRequestForm();
      updateRequestListUI();
      console.log(`Request "${deletedName}" deleted.`);
    }
  }

  // --- Export/Import Logic --- (exportData, importData - unchanged)
  function exportData() {
    /* ... unchanged ... */
    console.log("Exporting data...");
    try {
      saveDataToStorage();
      const dataString = JSON.stringify(appData, null, 2);
      const blob = new Blob([dataString], {
        type: 'application/json'
      });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      const timestamp = new Date().toISOString().slice(0, 16).replace(/[:T]/g, '-');
      link.download = `api-socket-tester-data-${timestamp}.json`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      console.log("Export initiated.");
    } catch (e) {
      console.error("Export failed:", e);
      alert("Failed to export data.");
    }
  }

  function importData() {
    /* ... unchanged ... */
    console.log("Import button clicked.");
    const file = importFileEl.files[0];
    if (!file) {
      alert("Select file.");
      return;
    }
    if (!file.name.endsWith('.json') && file.type !== 'application/json') {
      if (!confirm("File doesn't appear to be JSON. Import anyway?")) {
        importFileEl.value = '';
        return;
      }
    }
    const reader = new FileReader();
    reader.onload = function (event) {
      console.log("File read complete.");
      try {
        const importedText = event.target.result;
        const importedData = JSON.parse(importedText);
        console.log("Parsed imported data:", importedData);
        if (typeof importedData !== 'object' || importedData === null) throw new Error("Invalid object.");
        if (!Array.isArray(importedData.savedRequests)) throw new Error("Missing/invalid 'savedRequests'.");
        if (typeof importedData.socketSettings !== 'object' || importedData.socketSettings === null) importedData.socketSettings = {
          url: '',
          connectionHeaders: ''
        };
        if (typeof importedData.socketSettings.url !== 'string') importedData.socketSettings.url = '';
        if (typeof importedData.socketSettings.connectionHeaders !== 'string') importedData.socketSettings.connectionHeaders = '';
        if (typeof importedData.scratchpadContent !== 'string') importedData.scratchpadContent = '';
        if (confirm("OVERWRITE current data?")) {
          console.log("User confirmed import.");
          appData = importedData;
          saveDataToStorage();
          loadDataFromStorage();
          clearRequestForm();
          alert("Data imported!");
          importFileEl.value = '';
        } else {
          console.log("Import cancelled.");
          importFileEl.value = '';
        }
      } catch (e) {
        console.error("Import failed:", e);
        alert(`Import failed: ${e.message}.`);
        importFileEl.value = '';
      }
    };
    reader.onerror = function () {
      alert("Failed to read file.");
      importFileEl.value = '';
    };
    reader.readAsText(file);
  }

  // --- API Tester Logic ---

  async function handleApiSend() {
    console.log("Sending API request...");
    const url = apiUrlInput.value.trim();
    const method = apiMethodSelect.value;
    const headersText = apiHeadersTextarea.value;
    const bodyText = apiBodyTextarea.value;

    if (!url) {
      alert('Please enter an API URL.');
      return;
    }

    // Clear previous *live* response and test results
    apiStatusEl.textContent = 'Status: Sending...';
    apiResponseHeadersEl.textContent = '';
    apiResponseBodyEl.textContent = '';
    apiTestResultsEl.textContent = '';
    lastResponse = null; // Reset global live response tracker

    const requestOptions = {
      method: method,
      headers: parseHeaders(headersText)
    };
    if (['POST', 'PUT', 'PATCH'].includes(method.toUpperCase()) && bodyText) {
      let contentType = Object.keys(requestOptions.headers).find(k => k.toLowerCase() === 'content-type');
      contentType = contentType ? requestOptions.headers[contentType] : null;
      if (!contentType) {
        contentType = 'application/json';
        requestOptions.headers['Content-Type'] = contentType;
      }
      if (contentType.includes('application/json')) {
        try {
          JSON.parse(bodyText);
          requestOptions.body = bodyText;
        } catch (e) {
          apiStatusEl.textContent = 'Status: Error';
          apiResponseBodyEl.textContent = `Invalid JSON Body:\n${e.message}`;
          console.error("Invalid JSON Body:", e);
          return;
        }
      } else {
        requestOptions.body = bodyText;
      }
    }
    console.log("Fetch options:", requestOptions);

    let currentResponse = null; // Temp variable for this request's response
    try {
      const response = await fetch(url, requestOptions);
      console.log("Fetch response received:", response);
      // Build the response object
      currentResponse = {
        timestamp: new Date().toISOString(), // Add timestamp
        status: response.status,
        statusText: response.statusText,
        ok: response.ok,
        headers: {},
        body: null
      };
      response.headers.forEach((value, key) => {
        currentResponse.headers[key] = value;
      });
      const responseBodyText = await response.text();
      currentResponse.body = responseBodyText; // Store raw text initially
      const responseContentType = response.headers.get('content-type');
      if (responseContentType && responseContentType.includes('application/json') && responseBodyText) {
        try {
          currentResponse.body = JSON.parse(responseBodyText);
        } // Try to parse, overwrite body if successful
        catch (e) {
          console.warn("Failed to parse JSON response body:", e); /* Keep raw text */
        }
      }
    } catch (error) {
      console.error("API Request fetch Error:", error);
      currentResponse = { // Create an error response object
        timestamp: new Date().toISOString(),
        error: error.message,
        status: 'Network Error',
        statusText: ''
      };
    } finally {
      console.log("Final response object:", currentResponse);
      lastResponse = currentResponse; // Update global live response
      displayResponse(lastResponse); // Display the live response

      // *** NEW: Save response to the loaded saved request ***
      if (loadedRequestId && currentResponse) {
        const requestIndex = appData.savedRequests.findIndex(r => r.id === loadedRequestId);
        if (requestIndex !== -1) {
          console.log(`Saving response to saved request ID: ${loadedRequestId}`);
          appData.savedRequests[requestIndex].lastResponse = currentResponse;
          saveDataToStorage(); // Persist the updated saved request with its response
        } else {
          console.warn("Loaded request ID not found in saved data, cannot save response.");
        }
      } else if (!loadedRequestId) {
        console.log("Request sent was not a loaded saved request, response not saved persistently.");
      }
    }
  }

  // --- API Test Scripting Logic --- (handleRunApiTests - unchanged)
  function handleRunApiTests() {
    /* ... unchanged ... */
    console.log("Running API tests...");
    const script = apiTestScriptTextarea.value;
    apiTestResultsEl.textContent = 'Running tests...\n';
    if (!lastResponse) {
      apiTestResultsEl.textContent += 'Error: Send API request first.';
      return;
    }
    if (!script) {
      apiTestResultsEl.textContent += 'No test script.';
      return;
    }
    const testFunction = new Function('lastResponse', `let results = []; let consoleAssertions = []; const originalAssert = console.assert; console.assert = function(condition, message) { consoleAssertions.push({ condition: !!condition, message: message || 'Assertion failed' }); originalAssert.apply(console, arguments); }; try { ${script} results.push('Script finished.'); } catch (e) { results.push('Script Error: ' + e.message); results.push('Stack: ' + e.stack); } finally { console.assert = originalAssert; } return { results, consoleAssertions }; `);
    try {
      const {
        results,
        consoleAssertions
      } = testFunction(lastResponse);
      apiTestResultsEl.textContent += "--- Script Output ---\n" + results.join('\n') + '\n';
      apiTestResultsEl.textContent += "\n--- Assertions ---\n";
      if (consoleAssertions.length === 0) {
        apiTestResultsEl.textContent += "(No asserts found)\n";
      } else {
        consoleAssertions.forEach((assertion, index) => {
          apiTestResultsEl.textContent += `Assert ${index + 1}: [${assertion.condition ? 'PASS' : 'FAIL'}] ${assertion.message}\n`;
        });
      }
    } catch (e) {
      apiTestResultsEl.textContent = `Error running test func: ${e.message}\n${e.stack}`;
    }
    apiTestResultsEl.scrollTop = apiTestResultsEl.scrollHeight;
  }

  // --- Socket.IO Logic --- (handleSocketConnect, handleSocketDisconnect, handleSocketEmit, setupSocketListeners, updateSocketStatus - unchanged)
  function handleSocketConnect() {
    /* ... unchanged ... */
    console.log("Connect button clicked.");
    const url = socketUrlInput.value.trim();
    const headersText = socketConnHeadersTextarea.value.trim();
    if (!url) {
      alert('Enter Socket.IO URL.');
      return;
    }
    if (socket && socket.connected) {
      logSocketMessage('Already connected.');
      return;
    }
    if (socket) {
      console.log("Disconnecting previous socket...");
      socket.disconnect();
      socket = null;
    }
    logSocketMessage(`Connecting to ${url}...`);
    updateSocketStatus('connecting', `Connecting...`);
    const connectionOptions = {
      reconnectionAttempts: 3,
      extraHeaders: parseHeaders(headersText)
    };
    console.log("Socket opts:", connectionOptions);
    try {
      if (typeof io === 'undefined') throw new Error("Socket.IO client (io) not loaded.");
      socket = io(url, connectionOptions);
      setupSocketListeners();
    } catch (error) {
      console.error("Socket conn error:", error);
      logSocketMessage(`Connection error: ${error.message}`);
      updateSocketStatus('disconnected', `Error: ${error.message}`);
      socket = null;
    }
  }

  function handleSocketDisconnect() {
    /* ... unchanged ... */
    console.log("Disconnect button clicked.");
    if (socket && socket.connected) {
      logSocketMessage('Disconnecting...');
      socket.disconnect();
    } else if (socket) {
      logSocketMessage('Cleaning up non-connected socket.');
      socket.disconnect();
      socket = null;
      updateSocketStatus('disconnected', 'Disconnected');
    } else {
      logSocketMessage('Not connected.');
    }
  }

  function handleSocketEmit() {
    /* ... unchanged ... */
    console.log("Emit button clicked.");
    if (!socket || !socket.connected) {
      alert('Not connected.');
      return;
    }
    const event = socketEventNameInput.value.trim();
    const dataStr = socketEventDataTextarea.value.trim();
    if (!event) {
      alert('Enter event name.');
      return;
    }
    let dataToSend = null;
    if (dataStr) {
      try {
        dataToSend = JSON.parse(dataStr);
      } catch (e) {
        alert(`Invalid JSON:\n${e.message}`);
        return;
      }
    }
    logSocketMessage(`--> Emit '${event}': ${JSON.stringify(dataToSend, null, 2)}`);
    socket.emit(event, dataToSend);
  }

  function setupSocketListeners() {
    /* ... unchanged ... */
    if (!socket) {
      console.error("No socket for listeners.");
      return;
    }
    console.log("Setting socket listeners...");
    socket.offAny();
    socket.off('connect');
    socket.off('disconnect');
    socket.off('connect_error');
    socket.on('connect', () => {
      console.log("Socket connected.");
      logSocketMessage(`Connected! (ID: ${socket.id})`);
      updateSocketStatus('connected', `Connected to ${socketUrlInput.value}`);
    });
    socket.on('disconnect', (reason) => {
      console.log("Socket disconnected:", reason);
      logSocketMessage(`Disconnected: ${reason}`);
      updateSocketStatus('disconnected', `Disconnected: ${reason}`);
    });
    socket.on('connect_error', (error) => {
      console.error("Socket conn error event:", error);
      logSocketMessage(`Connection Error: ${error.message}`);
      if (error.data) {
        logSocketMessage(`  Details: ${JSON.stringify(error.data)}`)
      };
      updateSocketStatus('disconnected', `Connection Error: ${error.message}`);
    });
    socket.onAny((eventName, ...args) => {
      console.log(`Received event '${eventName}':`, args);
      logSocketMessage(`<-- Event '${eventName}':`);
      try {
        const formattedArgs = args.map(arg => (typeof arg === 'object' && arg !== null) ? JSON.stringify(arg, null, 2) : String(arg)).join('\n');
        logSocketMessage(formattedArgs);
      } catch (e) {
        logSocketMessage(`(Err format args: ${e.message}) ${String(args)}`);
      }
    });
  }

  function updateSocketStatus(statusType, message) {
    /* ... unchanged ... */
    console.log(`Updating socket status: ${statusType} - ${message}`);
    socketStatusEl.textContent = `Status: ${message}`;
    socketStatusEl.className = statusType;
    socketConnectBtn.disabled = (statusType === 'connected' || statusType === 'connecting');
    socketDisconnectBtn.disabled = (statusType === 'disconnected');
    socketEmitBtn.disabled = (statusType !== 'connected');
  }


  // --- Event Listener Attachments ---
  function attachEventListeners() {
    console.log("Attaching event listeners...");
    // Use try/catch for safety in case an element ID is wrong
    try {
      saveRequestBtn.addEventListener('click', saveRequest);
      requestListSelect.addEventListener('change', (e) => loadRequest(e.target.value));
      updateRequestBtn.addEventListener('click', updateSelectedRequest);
      deleteRequestBtn.addEventListener('click', deleteSelectedRequest);
      exportDataBtn.addEventListener('click', exportData);
      importDataBtn.addEventListener('click', importData);
      clearAllDataBtn.addEventListener('click', clearAllStoredData);
      sendApiRequestBtn.addEventListener('click', handleApiSend);
      runApiTestsBtn.addEventListener('click', handleRunApiTests);
      socketConnectBtn.addEventListener('click', handleSocketConnect);
      socketDisconnectBtn.addEventListener('click', handleSocketDisconnect);
      socketEmitBtn.addEventListener('click', handleSocketEmit);
      socketClearLogBtn.addEventListener('click', () => {
        socketLogAreaEl.textContent = '';
      });
      // Auto-save on blur
      socketUrlInput.addEventListener('blur', saveDataToStorage);
      socketConnHeadersTextarea.addEventListener('blur', saveDataToStorage);
      scratchpadTextarea.addEventListener('blur', saveDataToStorage);
      console.log("Event listeners attached successfully.");
    } catch (error) {
      console.error("FATAL: Failed to attach event listeners. Check HTML element IDs.", error);
      alert("Error initializing UI listeners. Some features might not work. Check console.");
    }
  }

  // --- Initialisation ---
  console.log("Running initialization...");
  loadDataFromStorage(); // Load saved state first
  attachEventListeners(); // Then attach listeners
  updateSocketStatus('disconnected', 'Disconnected'); // Set initial UI state for socket
  console.log("Initialization complete.");

}); // End DOMContentLoaded
