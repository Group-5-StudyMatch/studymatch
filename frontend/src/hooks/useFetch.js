// import the necessary React hooks for state, side effects, and memoization
import { useState, useEffect, useCallback } from 'react';

// Export the custom hook. It takes an API call function (RequestFn)
// and an optional array of dependencies (deps) to trigger re-fetches.
export function useFetch(RequestFn, deps = []) {
    //State to hold the successful API response payload
    const [data, setData] = useState(null);
    //State to hold any error that occurs during the API call
    const [error, setError] = useState(null);
    //State to indicate whether the API call is currently loading
    const [loading, setLoading] = useState(false);


    // useCallback caches this function so it isn't recreated on every render to prevent 
    // infinite loops in useEffect when dependencies change.   
    const load = useCallback(() => {
        // Reset loading and error states before making the API call
        setLoading(true);
        setError(null);

        // Execute the provided API call
        RequestFn()
            .then((res) => setData(res.data))
            // If it fails, it tries to catch an specific error message from backend
            .catch((err) => setError(err.response?.data?.message || err.message))
            // regardless of success or failure, turn off the loading state
            .finally(() => setLoading(false))
    }, deps)

    // automatically trigger load() function when the component mounts or when any of the dependencies change
    useEffect(() => {
        load()
    }, [load]);

    // Return the state variables and the load function so the component using this hook can display the UI
    // and trigger manual reloads if necessary.
    return { data, error, loading, reload: load }
    }