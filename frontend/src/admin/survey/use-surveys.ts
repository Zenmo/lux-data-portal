import {useState} from "react"
import {useOnce} from "../../hooks/use-once"
import {IndexSurvey, indexSurveysFromJson} from "joshi"
import {ZTOR_BASE_URL} from "../../services/ztor-fetch"
import {redirectToLogin} from "../../user/redirect-to-login"

type UseSurveyReturn = {
    loading: boolean,
    // for syncing the state
    changeSurvey: (newSurvey: IndexSurvey) => void,
    indexSurveys: IndexSurvey[],
    removeIndexSurvey: (id: string) => void,
}

export const useSurveys = (): UseSurveyReturn => {
    const [loading, setLoading] = useState(true)
    const [indexSurveys, setIndexSurveys] = useState<IndexSurvey[]>([])

    const changeSurvey = (newSurvey: IndexSurvey) => {
        setIndexSurveys(indexSurveys.map(survey => survey.id.toString() === newSurvey.id.toString() ? newSurvey : survey))
    }

    useOnce(async () => {
        try {
            const response = await fetch(ZTOR_BASE_URL + "/index-surveys", {
                credentials: "include",
            })
            if (response.status === 401) {
                redirectToLogin()
                return
            }
            setIndexSurveys(indexSurveysFromJson(await response.text()))
        } catch (error) {
            alert((error as Error).message)
        } finally {
            setLoading(false)
        }
    })


    const removeIndexSurvey = (id: string) => {
        setIndexSurveys(indexSurveys.filter(survey => survey.id.toString() !== id))
    }

    return {
        loading,
        changeSurvey,
        indexSurveys,
        removeIndexSurvey,
    }
}

