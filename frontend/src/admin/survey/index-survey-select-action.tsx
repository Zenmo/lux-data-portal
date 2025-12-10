import {FormEvent, useRef, useState} from "react"
import {ZTOR_BASE_URL} from "../../services/ztor-fetch"
import {buildDeeplinkUrl} from "../../components/company-survey-v2/deeplink"
import {OverlayPanel} from "primereact/overlaypanel"
import {Button} from "primereact/button"
import {IndexSurvey} from "joshi"
// @ts-ignore
import {CopyToClipboard} from "react-copy-to-clipboard"

type Props = {
    indexSurvey: IndexSurvey
}
const IndexSurveySelectAction = ({indexSurvey}: Props) => {
    const [deeplinkUrl, setDeeplinkUrl] = useState("")
    const overlayPanelRef = useRef<any>(null)
    const URL = `${ZTOR_BASE_URL}/company-surveys`

    const generateDeeplink = async (event: FormEvent) => {
        try {
            const response = await fetch(`${URL}/${indexSurvey.id}/deeplink`, {
                method: "POST",
                credentials: "include",
            })

            if (!response.ok) {
                throw new Error(`Failed to generate deeplink: ${response.statusText}`)
            }

            const data = await response.json()
            setDeeplinkUrl(buildDeeplinkUrl(data))

            if (overlayPanelRef.current) {
                overlayPanelRef.current.show(event)
            }
        } catch (error) {
            alert((error as Error).message)
        }
    }

    const handleSelectChange = async (e: any) => {
        const value = e.currentTarget.value

        switch (value) {
            case "json":
                window.open(`${URL}/${indexSurvey.id}`, "_blank")
                break
            case "share":
                await generateDeeplink(e)
                break
            default:
                break
        }

        e.currentTarget.value = ""
    }

    return (
        <>
            <select
                className="form-select bg-secondary-subtle rounded rounded-3 w-50 "
                onChange={handleSelectChange}
                value=""
            >
                <option value="">More actions</option>
                <option value="json">Survey Data (JSON)</option>
                <option value="share">Share</option>
            </select>

            <OverlayPanel ref={overlayPanelRef}>
                <div style={{
                    maxWidth: "30rem",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    flexDirection: "row",
                }}>
                    <div>
                        <a style={{maxWidth: "80%", wordBreak: "break-all"}} href={deeplinkUrl}>{deeplinkUrl}</a>
                    </div>
                    <div style={{paddingLeft: "1rem"}}>
                        <CopyToClipboard text={deeplinkUrl}>
                            <Button icon="pi pi-copy" outlined />
                        </CopyToClipboard>
                    </div>
                </div>
            </OverlayPanel>
        </>
    )
}

export {IndexSurveySelectAction}
