import React, {FunctionComponent} from "react"
import {css} from "@emotion/react"
import {BsEye, BsPlayFill} from "react-icons/bs"
import {AppHook} from "../services/appState"
import {Buurt} from "../services/wijkenbuurten/buurten"
import {AggregatedAreaData} from "./aggregated-area-data"
import {BuurtPicker} from "./buurt-picker"

const sidebarStyle = css({
    width: "280px",
    flexShrink: 0,
    backgroundColor: "#ffffff",
    borderRight: "1px solid #e5e7eb",
    display: "flex",
    flexDirection: "column",
    overflowY: "auto",
})

const headerStyle = css({
    padding: "1.75rem 1.5rem 1.25rem",
    borderBottom: "1px solid #e5e7eb",
})

const sectionLabelStyle = css({
    fontSize: "0.6rem",
    fontWeight: 700,
    letterSpacing: "0.15em",
    textTransform: "uppercase",
    color: "#9ca3af",
    margin: 0,
    marginBottom: "0.5rem",
})

const headingStyle = css({
    fontWeight: 700,
    color: "#1e3a5f",
    margin: 0,
})

const contentStyle = css({
    padding: "0 1.5rem",
    flexGrow: 1,
    overflowY: "auto",
})

const buurtPickerSectionStyle = css({
    borderTop: "1px solid #e5e7eb",
    paddingTop: "1rem",
    paddingBottom: "1rem",
})

const buurtPickerLabelStyle = css({
    fontSize: "0.7rem",
    fontWeight: 700,
    letterSpacing: "0.15em",
    textTransform: "uppercase",
    color: "#9ca3af",
    marginBottom: "0.75rem",
})

const footerStyle = css({
    padding: "1.25rem 1.5rem",
    borderTop: "1px solid #e5e7eb",
    display: "flex",
    flexDirection: "column",
    gap: "0.75rem",
    flexShrink: 0,
})

const primaryButtonStyle = css({
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "0.5rem",
    width: "100%",
    padding: "0.75rem 1rem",
    backgroundColor: "#1e3a5f",
    color: "#ffffff",
    border: "none",
    borderRadius: "0.5rem",
    fontSize: "0.95rem",
    fontWeight: 600,
    cursor: "pointer",
    transition: "background-color 0.15s ease-in-out",
    "&:hover": {
        backgroundColor: "#162d4a",
    },
})

const secondaryButtonStyle = css({
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "0.5rem",
    width: "100%",
    padding: "0.6rem 1rem",
    backgroundColor: "transparent",
    color: "#6b7280",
    border: "1px solid #e5e7eb",
    borderRadius: "0.5rem",
    fontSize: "0.875rem",
    cursor: "pointer",
    transition: "border-color 0.15s ease-in-out, color 0.15s ease-in-out",
    "&:hover": {
        borderColor: "#9ca3af",
        color: "#374151",
    },
})

export const SimulationSidebar: FunctionComponent<{
    appHook: AppHook
    onSelectBuurt: (buurt: Buurt) => void
    onStartSimulation: () => void
    onViewInput: () => void
}> = ({appHook, onSelectBuurt, onStartSimulation, onViewInput}) => (
    <div css={sidebarStyle}>
        <div css={headerStyle}>
            <p css={sectionLabelStyle}>Buurt statistieken</p>
            <p css={headingStyle}>Simuleer je buurt</p>
        </div>
        <div css={contentStyle}>
            <AggregatedAreaData appHook={appHook} />
            <div css={buurtPickerSectionStyle}>
                <p css={buurtPickerLabelStyle}>Selecteer buurt</p>
                <BuurtPicker onSelectBuurt={onSelectBuurt} />
            </div>
        </div>
        <div css={footerStyle}>
            <button css={primaryButtonStyle} onClick={onStartSimulation}>
                <BsPlayFill /> Start simulatie
            </button>
            <button css={secondaryButtonStyle} onClick={onViewInput}>
                <BsEye /> Bekijk simulatie input
            </button>
        </div>
    </div>
)
