package com.elanor.common.provider;

public interface SocialAuthProvider {
    SocialUserProfile verifyToken(String idToken);

    class SocialUserProfile {
        private String email;
        private String firstName;
        private String lastName;
        private String avatarUrl;

        public SocialUserProfile() {}

        public SocialUserProfile(String email, String firstName, String lastName, String avatarUrl) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.avatarUrl = avatarUrl;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
