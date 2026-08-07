import {ImportExcelButton} from "./import-excel-button"
import {NewSurveyButton} from "./new-survey-button"
import {useProjects} from "../project/use-projects"

export const AdminButtonRow = () => {
    const {projects} = useProjects()
    const hasEnergiekRegioProject = projects.some(p => p.energiekeRegioId != null)

    return (
        <div css={{display: "flex", gap: `${1/3}rem`}}>
            {hasEnergiekRegioProject && <ImportExcelButton/>}
            <NewSurveyButton/>
        </div>
    )
}
