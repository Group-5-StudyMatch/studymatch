// bring in the custom hook for fetching data from an API
import { useFetch } from '../hooks/useFetch';

// bring in the function that pings Spring Boot to see if its running
import {getHealth} from '../api/HealthApi';

// bring in the Status component to display loading and error states
import Status from '../components/Status';

// the main home page component.
export default function Home() {
    // check the health of the backend right away
    const {data, loading, error} = useFetch(getHealth)
    
    return (
        // the <> is a react fragment to group multiple elements without using extra <div> tags
        <>
            <h1>Study Match</h1>
            <Status loading={loading} error={error} />
            {data && <p>Backend is healthy! {JSON.stringify(data)}</p>}
        </>
        )
    }