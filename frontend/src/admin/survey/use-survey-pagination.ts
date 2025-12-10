import {useEffect, useMemo, useState} from "react"
import {FetchIndexSurveysRequest, IndexSurvey, IndexSurveyClient} from "joshi"
import {deleteSurvey as deleteSurveyFn} from "../delete-button"

type SurveyPaginationHook = {
    pending: boolean,
    indexSurveys: IndexSurvey[],
    totalCount?: number,
    request: FetchIndexSurveysRequest,
    setRequest: (request: FetchIndexSurveysRequest) => void,
    changeSurvey: (newSurvey: IndexSurvey) => void,
    deleteSurvey: (id: string) => void,
}

export function useSurveyPagination(): SurveyPaginationHook {
    const indexSurveyClient = useMemo(() => new IndexSurveyClient(), [])

    const [pending, setPending] = useState(true)
    const [request, setRequest] = useState<FetchIndexSurveysRequest>(
        new FetchIndexSurveysRequest().withLimit(10).withOffset(0)
    )
    const [indexSurveys, setIndexSurveys] = useState<IndexSurvey[]>([])
    const [totalCount, setTotalCount] = useState<number | undefined>()

    const changeSurvey = (newSurvey: IndexSurvey) => {
        setIndexSurveys(indexSurveys.map(survey => survey.id.toString() === newSurvey.id.toString() ? newSurvey : survey))
    }

    const removeIndexSurvey = (id: string) => {
        setIndexSurveys(indexSurveys.filter(survey => survey.id.toString() !== id))
    }

    const deleteSurvey = async (id: any) => {
        await deleteSurveyFn(
            {
                id: id,
                type: "company-surveys",
                onDelete: (id) => removeIndexSurvey(id.toString()),
                setPending: setPending,
            },
        )
    }

    useEffect(() => {
        setPending(true)
        // to prevent race conditions
        let aborted = false

        indexSurveyClient.fetchIndexSurveys(request)
            .then(response => {
                if (!aborted) {
                    // unpack into a new array to prevent stale datatable behavior
                    setIndexSurveys([...response.records.asJsReadonlyArrayView()])
                    setTotalCount(response.totalCount)
                    setPending(false)
                }
            })
        return () => {
            aborted = true
        }
    }, [request])

    return {
        pending,
        indexSurveys,
        totalCount,
        request,
        setRequest,
        changeSurvey,
        deleteSurvey,
    }
}
