import "leaflet/dist/leaflet.css"
import React, {FunctionComponent, useState} from "react"
import {css} from "@emotion/react"
import {AnyLogicDisplay, useAnyLogicActions} from "./components/any-logic"
import {MainMap} from "./components/main-map"
import {PandDataDisplay} from "./components/pand-display"
import {SimulationSidebar} from "./components/simulation-sidebar"
import {useApp} from "./services/appState"
import {assertDefined} from "./services/util"
import {Buurt} from "./services/wijkenbuurten/buurten"

const pageStyle = css({
    display: "flex",
    height: "calc(100vh - 74px)",
    overflow: "hidden",
})

const mapAreaStyle = css({
    flex: 1,
    display: "flex",
    flexDirection: "column",
    overflow: "hidden",
})

const pandPanelStyle = css({
    width: "280px",
    flexShrink: 0,
    borderLeft: "1px solid #e5e7eb",
    overflowY: "auto",
    padding: "1.25rem",
    backgroundColor: "#ffffff",
})

export const Simulation: FunctionComponent<{}> = () => {
    const appHook = useApp()
    const {setGeometry, getPandData, bag2dPanden} = appHook
    const {visible, onStartSimulation, onViewInput} = useAnyLogicActions(appHook)

    const [currentPandId, setCurrentPandId] = useState("")
    const [buurt, setBuurt] = useState<Buurt | undefined>()

    return (
        <div css={pageStyle}>
            <SimulationSidebar
                appHook={appHook}
                onSelectBuurt={buurt => {
                    setBuurt(buurt)
                    setGeometry(buurt.geometry)
                }}
                onStartSimulation={onStartSimulation}
                onViewInput={onViewInput}
            />

            <div css={mapAreaStyle}>
                <MainMap
                    bag2dPanden={bag2dPanden}
                    setGeometry={setGeometry}
                    setCurrentPandId={setCurrentPandId}
                    buurt={buurt}
                />
                <AnyLogicDisplay visible={visible} />
            </div>

            {currentPandId && getPandData(currentPandId) && (
                <div css={pandPanelStyle}>
                    <PandDataDisplay pandData={assertDefined(getPandData(currentPandId))} />
                </div>
            )}
        </div>
    )
}