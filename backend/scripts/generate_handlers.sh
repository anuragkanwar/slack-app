#!/bin/bash

# Configuration
PACKAGE="/home/anumax/projects/slackmessagebackend/src/main/java/com/anuragkanwar/slackmessagebackend/socket/handlers"
REQUEST_DTO_PACKAGE="/home/anumax/projects/slackmessagebackend/src/main/java/com/anuragkanwar/slackmessagebackend/model/dto/request"
RESPONSE_DTO_PACKAGE="/home/anumax/projects/slackmessagebackend/src/main/java/com/anuragkanwar/slackmessagebackend/model/dto/response"

# Create directories if they don't exist
mkdir -p "$PACKAGE"
mkdir -p "$REQUEST_DTO_PACKAGE"
mkdir -p "$RESPONSE_DTO_PACKAGE"

declare -a ENUM_VALUES=(
boot_room_list
boot_user_list
room_created
room_deleted
room_joined
room_left
room_rename
goodbye
hello
manual_presence_change
member_joined_room
member_left_room
message
presence_change
typing
user_typing
user_joined_workspace
user_left_workspace
)

# Function to convert path to package name
path_to_package() {
    echo "$1" | sed 's/.*src\/main\/java\///' | tr '/' '.'
}

for event_name in "${ENUM_VALUES[@]}"; do
  # Convert snake_case to CamelCase without spaces
  CLASS_NAME=$(echo "$event_name" |
    awk 'BEGIN{FS="_";OFS=""} {for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)}1')

  HANDLER_NAME="${CLASS_NAME}SocketEventHandler"
  REQUEST_DTO_NAME="${CLASS_NAME}RequestDto"
  RESPONSE_DTO_NAME="${CLASS_NAME}ResponseDto"

  # Get package names from paths
  HANDLER_PACKAGE=$(path_to_package "$PACKAGE")
  REQUEST_DTO_PACKAGE_NAME=$(path_to_package "$REQUEST_DTO_PACKAGE")
  RESPONSE_DTO_PACKAGE_NAME=$(path_to_package "$RESPONSE_DTO_PACKAGE")

  # Generate Handler Class
  cat > "$PACKAGE/$HANDLER_NAME.java" <<EOF
package $HANDLER_PACKAGE;

import $REQUEST_DTO_PACKAGE_NAME.$REQUEST_DTO_NAME;
import $RESPONSE_DTO_PACKAGE_NAME.$RESPONSE_DTO_NAME;
import com.corundumstudio.socketio.SocketIOClient;
import com.anuragkanwar.slackmessagebackend.socket.SocketEvent;

public class $HANDLER_NAME implements SocketEventHandler<$REQUEST_DTO_NAME, $RESPONSE_DTO_NAME> {

    @Override
    public $RESPONSE_DTO_NAME handle(SocketIOClient client, $REQUEST_DTO_NAME request) {
        // TODO: Implement $event_name event handling
        System.out.println("Handling $event_name event");

        $RESPONSE_DTO_NAME response = new $RESPONSE_DTO_NAME();
        // Initialize response fields
        return response;
    }

    @Override
    public SocketEvent getEventType() {
        return SocketEvent.$event_name;
    }

    @Override
    public Class<$REQUEST_DTO_NAME> getRequestType() {
        return $REQUEST_DTO_NAME.class;
    }

    @Override
    public Class<$RESPONSE_DTO_NAME> getResponseType() {
        return $RESPONSE_DTO_NAME.class;
    }
}
EOF

  # Generate Request DTO
  cat > "$REQUEST_DTO_PACKAGE/$REQUEST_DTO_NAME.java" <<EOF
package $REQUEST_DTO_PACKAGE_NAME;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class $REQUEST_DTO_NAME {
    // Request fields for $event_name
    // private String exampleField;

    // public String getExampleField() { return exampleField; }
    // public void setExampleField(String exampleField) { this.exampleField = exampleField; }
}
EOF

  # Generate Response DTO
  cat > "$RESPONSE_DTO_PACKAGE/$RESPONSE_DTO_NAME.java" <<EOF
package $RESPONSE_DTO_PACKAGE_NAME;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class $RESPONSE_DTO_NAME {
    // Response fields for $event_name
    // private boolean success;
    // private String message;

    // public boolean isSuccess() { return success; }
    // public void setSuccess(boolean success) { this.success = success; }
    // public String getMessage() { return message; }
    // public void setMessage(String message) { this.message = message; }
}
EOF

  echo "Generated:
  - Handler: $PACKAGE/$HANDLER_NAME.java
  - Request DTO: $REQUEST_DTO_PACKAGE/$REQUEST_DTO_NAME.java
  - Response DTO: $RESPONSE_DTO_PACKAGE/$RESPONSE_DTO_NAME.java"
done

echo "Generation complete! Created ${#ENUM_VALUES[@]} event handlers with DTOs."