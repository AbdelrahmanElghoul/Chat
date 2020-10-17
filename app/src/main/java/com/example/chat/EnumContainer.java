package com.example.chat;

public class EnumContainer{

    public static enum permissions {

        READ_EXTERNAL_STORAGE("Manifest.permission.READ_EXTERNAL_STORAGE");
        public final String Value;
        private permissions(String Value) {
            this.Value = Value;
        }

        @Override
        public String toString() {
            return String.valueOf(hashCode());
        }

    }
    public static enum RequestCode {

        ASK_FOR_PERMISSION(1),
        GET_IMAGE_RESULT(1932);

        public final int Value;

        private RequestCode(int Value) {
            this.Value = Value;
        }

    }
}
