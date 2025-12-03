import {redirectToLogin} from "../user/redirect-to-login"

export const ZTOR_BASE_URL = import.meta.env.VITE_ZTOR_URL
// default way to communicate with the backend
export async function ztorFetch<T>(
    path: string,
    requestInit: RequestInit = {},
): Promise<T> {
    const url = new URL(ZTOR_BASE_URL + path)

    requestInit.credentials = "include"

    const response = await fetch(url, requestInit)
    if (response.status === 401) {
        redirectToLogin()
        throw new Error("Redirecting to login")
    }

    if (response.status < 200 || response.status > 299) {
        throw new Error(await response.text())
    }

    if (response.headers.get("Content-Length") === "0") {
        return undefined as T
    }

    return await response.json()
}


