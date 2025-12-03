import {FunctionComponent, useRef} from "react"
import {Tooltip} from "primereact/tooltip"

export const ValidationIndicator: FunctionComponent<{ messages: readonly string[] }> = ({messages}) => {
    if (messages.length == 0) {
        return null
    }

    const ref = useRef<HTMLElement>(null)

    return (
        <span
            ref={ref}
            css={{
                color: "red",
                display: "flex",
                alignItems: "center",
                gap: "0.2rem",
                padding: "0.5rem",
                marginRight: "-0.5rem"
            }}
        >
            <span>{messages.length}</span>
            <i className="pi pi-exclamation-triangle" />
            <Tooltip target={ref} position="bottom">
                <ul style={{marginBottom: 0}}>
                    {messages.map((message, i) => (
                        <li key={i}>{message}</li>
                    ))}
                </ul>
            </Tooltip>
        </span>
    )
}
