import {ZTOR_BASE_URL} from "../services/ztor-fetch"

type DeleteSurveyProps = {
    type: string,
    id: any,
    onDelete: (id: any) => void,
    setPending: (pending: boolean) => void
}

export const deleteSurvey = async ({type, id, onDelete, setPending}: DeleteSurveyProps) => {
    setPending(true)
    try {
        if (!confirm("Uitvraag verwijderen?")) {
            return
        }
        const response = await fetch(`${ZTOR_BASE_URL}/${type}/${id}`, {
            method: "DELETE",
            credentials: "include",
        })
        if (!response.ok) {
            throw new Error(`Could not delete: ${response.status} ${response.statusText}`)
        }
        onDelete(id)
    } catch (error) {
        alert((error as Error).message)
    } finally {
        setPending(false)
    }
}
