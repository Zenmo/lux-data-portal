import {useRef, useState} from 'react'
import {startSimulation} from '../services/any-logic/any-logic-client'
import {appStateToScenarioInput} from '../services/any-logic/scenario-input'
import {AppHook} from '../services/appState'

const SIMULATION_DIV_ID = 'any-logic'

export const useAnyLogicActions = (appHook: AppHook) => {
    const [visible, setVisible] = useState(false)

    const onStartSimulation = async () => {
        setVisible(true)
        await startSimulation(SIMULATION_DIV_ID, appHook)
    }

    const onViewInput = () => {
        const input = JSON.stringify(appStateToScenarioInput(appHook))
        const newWindow = window.open('data:application/json,' + encodeURIComponent(input), '_blank')
        if (newWindow === null) {
            throw new Error('Can\'t open new window')
        }
        newWindow.focus()
    }

    return {visible, onStartSimulation, onViewInput}
}

export const AnyLogicDisplay = ({visible}: {visible: boolean}) => {
    const ref = useRef(null)
    return (
        <div id={SIMULATION_DIV_ID} ref={ref}
             style={{flexGrow: 1, display: visible ? 'block' : 'none'}}>
            <p>Bezig met laden...</p>
        </div>
    )
}