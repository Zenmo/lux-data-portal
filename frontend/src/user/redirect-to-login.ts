
export function redirectToLogin() {
    window.location.href = import.meta.env.VITE_ZTOR_URL + "/login?redirectUrl=" + encodeURIComponent(window.location.href)
}
